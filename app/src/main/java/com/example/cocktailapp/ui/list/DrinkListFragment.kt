package com.example.cocktailapp.ui.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import androidx.core.os.bundleOf
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.cocktailapp.R
import com.example.cocktailapp.data.model.Drink
import com.example.cocktailapp.databinding.FragmentDrinkListBinding
import com.example.cocktailapp.ui.common.UiState
import com.example.cocktailapp.ui.common.hide
import com.example.cocktailapp.ui.common.message
import com.example.cocktailapp.ui.common.show
import com.example.cocktailapp.ui.detail.DrinkDetailFragment
import kotlinx.coroutines.launch

class DrinkListFragment : Fragment() {

    // [Bato] ViewBinding con _binding nullable + binding no nulo.
    //   Antes: lateinit var recyclerView / searchBox / progressBar + findViewById para cada una.
    //   Por qué: las vistas vienen tipadas y en un solo objeto. _binding se pone en null en
    //   onDestroyView: el Fragment vive más que su vista, y si se guardara la vista vieja
    //   quedaría ocupando memoria (fuga de memoria).
    private var _binding: FragmentDrinkListBinding? = null
    private val binding get() = _binding!!

    // [Bato] El Fragment ya no llama a Retrofit: pide todo al ViewModel.
    //   Antes: lifecycleScope.launch { RetrofitClient.api.searchByName(...) } dentro del Fragment.
    //   Por qué: el ViewModel sobrevive a la rotación y separa "qué mostrar" de "cómo dibujarlo".
    private val viewModel: DrinkListViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentDrinkListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // El adapter se crea con cada vista nueva para no retener un RecyclerView viejo.
        // El layoutManager ahora se declara en el XML (app:layoutManager).
        val adapter = DrinkAdapter { drink -> openDetail(drink) }
        binding.recyclerDrinks.adapter = adapter

        setupSearch()
        setupIngredientFilter()

        // [Mejora] viewLifecycleOwner.lifecycleScope + repeatOnLifecycle(STARTED).
        //   Antes: lifecycleScope (el del Fragment), que sigue vivo cuando la vista ya se
        //   destruyó, por ejemplo al abrir el detalle.
        //   Por qué: así se escucha al ViewModel solo mientras la pantalla está visible,
        //   y la escucha se corta sola cuando la vista se destruye.
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch { viewModel.state.collect { state -> render(state, adapter) } }
                launch { viewModel.ingredients.collect { ingredients -> showIngredients(ingredients) } }
            }
        }
    }

    private fun setupSearch() {
        binding.editSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val query = binding.editSearch.text?.toString().orEmpty()
                if (query.isNotBlank()) {
                    viewModel.searchByName(query)
                    // Si se busca por nombre, se limpia el filtro para que no confunda.
                    binding.dropdownIngredient.setText("", false)
                    hideKeyboard()
                }
                true
            } else {
                false
            }
        }
    }

    // [Bato] Filtro por ingrediente.
    //   Diferencias con Bato:
    //   - Él usaba un Spinner con "Selecciona un ingrediente" como primer elemento, y en la
    //     Activity comparaba ese mismo texto para ignorarlo (el texto estaba repetido en dos
    //     archivos: si se cambiaba en uno, el filtro se rompía).
    //   - Acá es un menú desplegable de Material donde además se puede escribir para filtrar
    //     ("vod" → Vodka). El texto de ayuda es un hint del XML, no un elemento falso de la lista.
    private fun setupIngredientFilter() {
        binding.dropdownIngredient.setOnItemClickListener { parent, _, position, _ ->
            val ingredient = parent.getItemAtPosition(position) as String
            viewModel.filterByIngredient(ingredient)
            binding.editSearch.setText("")
            binding.dropdownIngredient.clearFocus()
            hideKeyboard()
        }
    }

    private fun showIngredients(ingredients: List<String>) {
        val adapter = ArrayAdapter(requireContext(), R.layout.item_ingredient, ingredients)
        binding.dropdownIngredient.setAdapter(adapter)
        binding.layoutIngredient.isEnabled = ingredients.isNotEmpty()
    }

    // [Mejora] Cada estado se dibuja en un solo lugar.
    //   Antes: el spinner y la lista se mostraban/ocultaban en distintos puntos de searchDrinks().
    private fun render(state: UiState<DrinkListContent>, adapter: DrinkAdapter) {
        binding.progressList.isVisible = state is UiState.Loading

        when (state) {
            is UiState.Loading -> {
                binding.recyclerDrinks.isVisible = false
                binding.textResultsTitle.isVisible = false
                binding.message.hide()
            }

            is UiState.Success -> {
                val (search, drinks) = state.data
                adapter.submitList(drinks)
                binding.textResultsTitle.isVisible = true
                binding.textResultsTitle.text = titleFor(search, drinks.size)

                if (drinks.isEmpty()) {
                    // [Mejora] Mensaje en pantalla en vez de Toast("Sin resultados").
                    binding.recyclerDrinks.isVisible = false
                    binding.message.show(R.drawable.ic_local_bar, emptyTextFor(search))
                } else {
                    binding.recyclerDrinks.isVisible = true
                    binding.message.hide()
                }
            }

            is UiState.Error -> {
                // [Mejora] Mensaje en pantalla con "Reintentar" en vez de Toast("Error de conexión").
                binding.recyclerDrinks.isVisible = false
                binding.textResultsTitle.isVisible = false
                binding.message.show(
                    icon = R.drawable.ic_cloud_off,
                    text = state.message(requireContext()),
                    onRetry = if (state.canRetry) viewModel::retry else null
                )
            }
        }
    }

    private fun titleFor(search: DrinkSearch, count: Int): String {
        val base = when (search) {
            is DrinkSearch.ByName -> getString(R.string.results_by_name, search.query)
            is DrinkSearch.ByIngredient -> getString(R.string.results_by_ingredient, search.ingredient)
        }
        val total = resources.getQuantityString(R.plurals.drinks_count, count, count)
        return "$base · $total"
    }

    private fun emptyTextFor(search: DrinkSearch): String = when (search) {
        is DrinkSearch.ByName -> getString(R.string.empty_by_name, search.query)
        is DrinkSearch.ByIngredient -> getString(R.string.empty_by_ingredient, search.ingredient)
    }

    private fun openDetail(drink: Drink) {
        // [Mejora] Clave del argumento en una constante (DrinkDetailFragment.ARG_DRINK_ID).
        //   Antes: el texto "drinkId" escrito a mano acá y en el detalle.
        //   Por qué: si se escribe distinto en un lugar, el detalle no recibe nada y nadie avisa.
        //   (Bato tenía el mismo problema con "COCKTAIL_ID", repetido en 3 lugares.)
        findNavController().navigate(
            R.id.action_drinkList_to_drinkDetail,
            bundleOf(DrinkDetailFragment.ARG_DRINK_ID to drink.id)
        )
    }

    private fun hideKeyboard() {
        binding.editSearch.clearFocus()
        WindowCompat.getInsetsController(requireActivity().window, binding.root)
            .hide(WindowInsetsCompat.Type.ime())
    }

    // [Bato] Limpiar el binding cuando se destruye la vista (evita fugas de memoria).
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

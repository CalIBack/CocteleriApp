package com.example.cocktailapp.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import com.example.cocktailapp.R
import com.example.cocktailapp.data.model.Drink
import com.example.cocktailapp.databinding.FragmentDrinkDetailBinding
import com.example.cocktailapp.ui.common.UiState
import com.example.cocktailapp.ui.common.hide
import com.example.cocktailapp.ui.common.message
import com.example.cocktailapp.ui.common.show
import com.google.android.material.chip.Chip
import kotlinx.coroutines.launch

class DrinkDetailFragment : Fragment() {

    // [Bato] ViewBinding (ver la explicación en DrinkListFragment).
    //   Antes: seis findViewById, uno por cada vista del detalle.
    private var _binding: FragmentDrinkDetailBinding? = null
    private val binding get() = _binding!!

    // [Bato] El detalle pide los datos a su ViewModel.
    //   Antes: el Fragment leía arguments, llamaba a Retrofit y manejaba los errores él mismo.
    //   El id ahora lo lee el ViewModel (SavedStateHandle), no el Fragment.
    private val viewModel: DrinkDetailViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentDrinkDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state -> render(state) }
            }
        }
    }

    private fun render(state: UiState<Drink>) {
        binding.progressDetail.isVisible = state is UiState.Loading
        binding.scrollDetail.isVisible = state is UiState.Success

        when (state) {
            is UiState.Loading -> binding.message.hide()
            is UiState.Success -> {
                binding.message.hide()
                showDrink(state.data)
            }
            is UiState.Error -> binding.message.show(
                icon = R.drawable.ic_cloud_off,
                text = state.message(requireContext()),
                onRetry = if (state.canRetry) viewModel::retry else null
            )
        }
    }

    private fun showDrink(drink: Drink) {
        binding.textDetailName.text = drink.name
        binding.textInstructions.text = drink.instructions?.takeIf { it.isNotBlank() }
            ?: getString(R.string.no_instructions)

        // Se arma el texto con viñetas. Bato hacía esto en el Fragment con su propia
        // lista de 15 pares; acá se reutiliza getIngredientsList() del modelo.
        binding.textIngredients.text = drink.getIngredientsList().joinToString("\n") { "•  $it" }

        // [Mejora] Etiquetas con categoría, tipo y vaso (solo las que tienen dato).
        setChip(binding.chipCategory, drink.category)
        setChip(binding.chipAlcoholic, drink.alcoholic)
        setChip(binding.chipGlass, drink.glass)
        binding.chipGroupInfo.isVisible =
            binding.chipCategory.isVisible || binding.chipAlcoholic.isVisible || binding.chipGlass.isVisible

        // La foto ya tiene esquinas redondeadas por la tarjeta que la contiene (XML).
        Glide.with(this)
            .load(drink.thumbnail)
            .placeholder(R.drawable.ic_local_bar)
            .error(R.drawable.ic_local_bar)
            .into(binding.imageDetail)
    }

    private fun setChip(chip: Chip, text: String?) {
        chip.text = text
        chip.isVisible = !text.isNullOrBlank()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        // [Mejora] Clave del argumento en un solo lugar.
        //   Antes: "drinkId" escrito a mano en la lista y en el detalle.
        //   Debe coincidir con <argument android:name="drinkId"> del nav_graph.xml.
        const val ARG_DRINK_ID = "drinkId"
    }
}

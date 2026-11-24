package com.example.cocktailapp.ui.lista

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
import com.example.cocktailapp.databinding.FragmentListaBinding
import com.example.cocktailapp.datos.modelo.Coctel
import com.example.cocktailapp.ui.comun.EstadoUi
import com.example.cocktailapp.ui.comun.mostrar
import com.example.cocktailapp.ui.comun.ocultar
import com.example.cocktailapp.ui.comun.texto
import com.example.cocktailapp.ui.detalle.DetalleFragment
import kotlinx.coroutines.launch

class ListaFragment : Fragment() {

    // _binding existe solo mientras existe la vista; binding es el acceso cómodo
    private var _binding: FragmentListaBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ListaViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentListaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = CoctelAdapter { coctel -> abrirDetalle(coctel) }
        binding.recyclerCocteles.adapter = adapter

        configurarBuscador()
        configurarFiltroIngrediente()

        // Escucha al ViewModel solo mientras la pantalla está visible
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch { viewModel.estado.collect { estado -> mostrarEstado(estado, adapter) } }
                launch { viewModel.ingredientes.collect { ingredientes -> cargarOpcionesIngrediente(ingredientes) } }
            }
        }
    }

    private fun configurarBuscador() {
        binding.editBusqueda.setOnEditorActionListener { _, accion, _ ->
            if (accion != EditorInfo.IME_ACTION_SEARCH) return@setOnEditorActionListener false

            val texto = binding.editBusqueda.text?.toString().orEmpty()
            if (texto.isNotBlank()) {
                viewModel.buscarPorNombre(texto)
                binding.desplegableIngrediente.setText("", false)
                ocultarTeclado()
            }
            true
        }
    }

    private fun configurarFiltroIngrediente() {
        binding.desplegableIngrediente.setOnItemClickListener { lista, _, posicion, _ ->
            val ingrediente = lista.getItemAtPosition(posicion) as String
            viewModel.filtrarPorIngrediente(ingrediente)
            binding.editBusqueda.setText("")
            binding.desplegableIngrediente.clearFocus()
            ocultarTeclado()
        }
    }

    private fun cargarOpcionesIngrediente(ingredientes: List<String>) {
        val opciones = ArrayAdapter(requireContext(), R.layout.item_ingrediente, ingredientes)
        binding.desplegableIngrediente.setAdapter(opciones)
        binding.layoutIngrediente.isEnabled = ingredientes.isNotEmpty()
    }

    private fun mostrarEstado(estado: EstadoUi<ResultadoBusqueda>, adapter: CoctelAdapter) {
        binding.progresoLista.isVisible = estado is EstadoUi.Cargando

        when (estado) {
            is EstadoUi.Cargando -> {
                binding.recyclerCocteles.isVisible = false
                binding.textoTituloResultados.isVisible = false
                binding.mensaje.ocultar()
            }

            is EstadoUi.Exito -> {
                val (busqueda, cocteles) = estado.datos
                adapter.submitList(cocteles)
                binding.textoTituloResultados.isVisible = true
                binding.textoTituloResultados.text = tituloResultados(busqueda, cocteles.size)

                if (cocteles.isEmpty()) {
                    binding.recyclerCocteles.isVisible = false
                    binding.mensaje.mostrar(R.drawable.ic_local_bar, textoSinResultados(busqueda))
                } else {
                    binding.recyclerCocteles.isVisible = true
                    binding.mensaje.ocultar()
                }
            }

            is EstadoUi.Error -> {
                binding.recyclerCocteles.isVisible = false
                binding.textoTituloResultados.isVisible = false
                binding.mensaje.mostrar(
                    icono = R.drawable.ic_cloud_off,
                    texto = estado.texto(requireContext()),
                    alReintentar = if (estado.sePuedeReintentar) viewModel::reintentar else null
                )
            }
        }
    }

    private fun tituloResultados(busqueda: Busqueda, cantidad: Int): String {
        val titulo = when (busqueda) {
            is Busqueda.PorNombre -> getString(R.string.resultados_por_nombre, busqueda.nombre)
            is Busqueda.PorIngrediente -> getString(R.string.resultados_por_ingrediente, busqueda.ingrediente)
        }
        val total = resources.getQuantityString(R.plurals.cantidad_cocteles, cantidad, cantidad)
        return "$titulo · $total"
    }

    private fun textoSinResultados(busqueda: Busqueda): String = when (busqueda) {
        is Busqueda.PorNombre -> getString(R.string.sin_resultados_nombre, busqueda.nombre)
        is Busqueda.PorIngrediente -> getString(R.string.sin_resultados_ingrediente, busqueda.ingrediente)
    }

    private fun abrirDetalle(coctel: Coctel) {
        findNavController().navigate(
            R.id.accion_lista_a_detalle,
            bundleOf(DetalleFragment.ARG_ID_COCTEL to coctel.id)
        )
    }

    private fun ocultarTeclado() {
        binding.editBusqueda.clearFocus()
        WindowCompat.getInsetsController(requireActivity().window, binding.root)
            .hide(WindowInsetsCompat.Type.ime())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

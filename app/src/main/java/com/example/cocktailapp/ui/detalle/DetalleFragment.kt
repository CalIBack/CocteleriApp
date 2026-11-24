package com.example.cocktailapp.ui.detalle

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
import com.example.cocktailapp.databinding.FragmentDetalleBinding
import com.example.cocktailapp.datos.modelo.Coctel
import com.example.cocktailapp.ui.comun.EstadoUi
import com.example.cocktailapp.ui.comun.mostrar
import com.example.cocktailapp.ui.comun.ocultar
import com.example.cocktailapp.ui.comun.texto
import com.google.android.material.chip.Chip
import kotlinx.coroutines.launch

class DetalleFragment : Fragment() {

    private var _binding: FragmentDetalleBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DetalleViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentDetalleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.estado.collect { estado -> mostrarEstado(estado) }
            }
        }
    }

    private fun mostrarEstado(estado: EstadoUi<Coctel>) {
        binding.progresoDetalle.isVisible = estado is EstadoUi.Cargando
        binding.scrollDetalle.isVisible = estado is EstadoUi.Exito

        when (estado) {
            is EstadoUi.Cargando -> binding.mensaje.ocultar()
            is EstadoUi.Exito -> {
                binding.mensaje.ocultar()
                mostrarCoctel(estado.datos)
            }
            is EstadoUi.Error -> binding.mensaje.mostrar(
                icono = R.drawable.ic_cloud_off,
                texto = estado.texto(requireContext()),
                alReintentar = if (estado.sePuedeReintentar) viewModel::reintentar else null
            )
        }
    }

    private fun mostrarCoctel(coctel: Coctel) {
        binding.textoNombreDetalle.text = coctel.nombre
        binding.textoInstrucciones.text = coctel.instrucciones?.takeIf { it.isNotBlank() }
            ?: getString(R.string.sin_instrucciones)
        binding.textoIngredientes.text = coctel.obtenerIngredientes().joinToString("\n") { "•  $it" }

        mostrarEtiqueta(binding.chipCategoria, coctel.categoria)
        mostrarEtiqueta(binding.chipAlcohol, coctel.alcohol)
        mostrarEtiqueta(binding.chipVaso, coctel.vaso)
        binding.grupoEtiquetas.isVisible =
            binding.chipCategoria.isVisible || binding.chipAlcohol.isVisible || binding.chipVaso.isVisible

        Glide.with(this)
            .load(coctel.miniatura)
            .placeholder(R.drawable.ic_local_bar)
            .error(R.drawable.ic_local_bar)
            .into(binding.imagenDetalle)
    }

    private fun mostrarEtiqueta(etiqueta: Chip, texto: String?) {
        etiqueta.text = texto
        etiqueta.isVisible = !texto.isNullOrBlank()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        // Tiene que coincidir con el <argument> del nav_graph
        const val ARG_ID_COCTEL = "idCoctel"
    }
}

package com.example.cocktailapp.ui.detalle

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cocktailapp.R
import com.example.cocktailapp.datos.modelo.Coctel
import com.example.cocktailapp.datos.repositorio.CoctelRepositorio
import com.example.cocktailapp.ui.comun.EstadoUi
import com.example.cocktailapp.ui.comun.aEstadoError
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// SavedStateHandle trae los argumentos del Fragment (el id que mandó la lista)
class DetalleViewModel(argumentos: SavedStateHandle) : ViewModel() {

    private val repositorio = CoctelRepositorio()
    private val idCoctel: String? = argumentos[DetalleFragment.ARG_ID_COCTEL]

    private val _estado = MutableStateFlow<EstadoUi<Coctel>>(EstadoUi.Cargando)
    val estado: StateFlow<EstadoUi<Coctel>> = _estado.asStateFlow()

    init {
        cargar()
    }

    fun reintentar() = cargar()

    private fun cargar() {
        val id = idCoctel
        if (id == null) {
            _estado.value = EstadoUi.Error(R.string.error_sin_id, sePuedeReintentar = false)
            return
        }

        viewModelScope.launch {
            _estado.value = EstadoUi.Cargando
            _estado.value = try {
                val coctel = repositorio.obtenerPorId(id)
                if (coctel != null) EstadoUi.Exito(coctel)
                else EstadoUi.Error(R.string.error_no_encontrado, sePuedeReintentar = false)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                e.aEstadoError()
            }
        }
    }
}

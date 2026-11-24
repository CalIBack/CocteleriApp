package com.example.cocktailapp.ui.lista

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cocktailapp.datos.modelo.Coctel
import com.example.cocktailapp.datos.repositorio.CoctelRepositorio
import com.example.cocktailapp.ui.comun.EstadoUi
import com.example.cocktailapp.ui.comun.aEstadoError
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface Busqueda {
    data class PorNombre(val nombre: String) : Busqueda
    data class PorIngrediente(val ingrediente: String) : Busqueda
}

data class ResultadoBusqueda(
    val busqueda: Busqueda,
    val cocteles: List<Coctel>
)

class ListaViewModel : ViewModel() {

    private val repositorio = CoctelRepositorio()

    // _estado lo modifica solo el ViewModel; la pantalla observa estado (solo lectura)
    private val _estado = MutableStateFlow<EstadoUi<ResultadoBusqueda>>(EstadoUi.Cargando)
    val estado: StateFlow<EstadoUi<ResultadoBusqueda>> = _estado.asStateFlow()

    private val _ingredientes = MutableStateFlow<List<String>>(emptyList())
    val ingredientes: StateFlow<List<String>> = _ingredientes.asStateFlow()

    private var ultimaBusqueda: Busqueda = Busqueda.PorNombre(BUSQUEDA_INICIAL)
    private var tareaBusqueda: Job? = null

    // Se ejecuta una sola vez: al girar el celular no se repite
    init {
        buscar(ultimaBusqueda)
        cargarIngredientes()
    }

    fun buscarPorNombre(texto: String) {
        val nombre = texto.trim()
        if (nombre.isNotEmpty()) buscar(Busqueda.PorNombre(nombre))
    }

    fun filtrarPorIngrediente(ingrediente: String) {
        buscar(Busqueda.PorIngrediente(ingrediente))
    }

    fun reintentar() {
        buscar(ultimaBusqueda)
        if (_ingredientes.value.isEmpty()) cargarIngredientes()
    }

    private fun buscar(busqueda: Busqueda) {
        ultimaBusqueda = busqueda
        // Si había una búsqueda en curso se cancela, así una respuesta vieja no pisa a la nueva
        tareaBusqueda?.cancel()
        tareaBusqueda = viewModelScope.launch {
            _estado.value = EstadoUi.Cargando
            _estado.value = try {
                val cocteles = when (busqueda) {
                    is Busqueda.PorNombre -> repositorio.buscarPorNombre(busqueda.nombre)
                    is Busqueda.PorIngrediente -> repositorio.filtrarPorIngrediente(busqueda.ingrediente)
                }
                EstadoUi.Exito(ResultadoBusqueda(busqueda, cocteles))
            } catch (e: CancellationException) {
                throw e // cancelar no es un error
            } catch (e: Exception) {
                e.aEstadoError()
            }
        }
    }

    private fun cargarIngredientes() {
        viewModelScope.launch {
            try {
                _ingredientes.value = repositorio.obtenerIngredientes()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                // Sin ingredientes el filtro queda vacío; la búsqueda por nombre sigue andando
            }
        }
    }

    companion object {
        const val BUSQUEDA_INICIAL = "margarita"
    }
}

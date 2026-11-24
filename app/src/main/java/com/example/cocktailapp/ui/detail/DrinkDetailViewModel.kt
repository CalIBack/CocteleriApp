package com.example.cocktailapp.ui.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cocktailapp.R
import com.example.cocktailapp.data.model.Drink
import com.example.cocktailapp.data.repository.CocktailRepository
import com.example.cocktailapp.ui.common.UiState
import com.example.cocktailapp.ui.common.toErrorState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// [Bato] ViewModel para la pantalla de detalle.
//   Antes: DrinkDetailFragment llamaba a Retrofit y llenaba las vistas por su cuenta.
//   Por qué: el cóctel cargado sobrevive a la rotación y el Fragment queda solo para dibujar.
//
// [Mejora] Diferencias con Bato:
//   1. ViewModel propio del detalle. Bato reutilizaba el de la lista, que al crearse pedía
//      todos los ingredientes: cada vez que abrías un cóctel había una llamada inútil.
//   2. El id se lee del SavedStateHandle, que Android llena solo con los argumentos del
//      Fragment. La carga se hace en init, UNA vez. Bato llamaba a fetchCocktailDetails()
//      en onViewCreated, así que al girar el celular lo volvía a pedir.
class DrinkDetailViewModel(savedStateHandle: SavedStateHandle) : ViewModel() {

    private val repository = CocktailRepository()

    private val drinkId: String? = savedStateHandle[DrinkDetailFragment.ARG_DRINK_ID]

    private val _state = MutableStateFlow<UiState<Drink>>(UiState.Loading)
    val state: StateFlow<UiState<Drink>> = _state.asStateFlow()

    init {
        load()
    }

    fun retry() = load()

    private fun load() {
        val id = drinkId
        if (id == null) {
            // Caso defensivo: no llegó el id. Reintentar no lo arreglaría.
            _state.value = UiState.Error(R.string.error_missing_id, canRetry = false)
            return
        }
        viewModelScope.launch {
            _state.value = UiState.Loading
            _state.value = try {
                val drink = repository.getById(id)
                if (drink != null) {
                    UiState.Success(drink)
                } else {
                    // [Mejora] Bato no avisaba nada si la API no tenía ese id (pantalla en blanco).
                    UiState.Error(R.string.error_not_found, canRetry = false)
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                e.toErrorState()
            }
        }
    }
}

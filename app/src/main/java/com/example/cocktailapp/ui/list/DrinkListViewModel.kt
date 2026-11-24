package com.example.cocktailapp.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cocktailapp.data.model.Drink
import com.example.cocktailapp.data.repository.CocktailRepository
import com.example.cocktailapp.ui.common.UiState
import com.example.cocktailapp.ui.common.toErrorState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// Qué se buscó: por nombre o por ingrediente. La pantalla lo usa para el título
// ("Resultados para «margarita»" o "Cócteles con Vodka") y el ViewModel para reintentar.
sealed interface DrinkSearch {
    data class ByName(val query: String) : DrinkSearch
    data class ByIngredient(val ingredient: String) : DrinkSearch
}

// Lo que la lista necesita para dibujarse cuando la búsqueda salió bien.
data class DrinkListContent(
    val search: DrinkSearch,
    val drinks: List<Drink>
)

// [Bato] ViewModel para la pantalla de lista (patrón MVVM).
//   Antes: DrinkListFragment llamaba a Retrofit, manejaba el spinner y guardaba todo.
//   Por qué: el ViewModel sobrevive cuando girás el celular. Antes, al girar, el Fragment
//   se recreaba, se perdía la búsqueda y se volvía a pedir "margarita" a internet.
//   Ahora el Fragment solo dibuja lo que el ViewModel le dice.
//
// [Mejora] Diferencias con el ViewModel de Bato:
//   1. Un ViewModel por pantalla. Bato usaba uno solo para lista y detalle, y como su init
//      pedía la lista de ingredientes, abrir cada cóctel hacía una llamada que nadie usaba.
//   2. StateFlow + UiState en vez de LiveData sueltos (ver ui/common/UiState.kt).
//   3. La búsqueda inicial se hace en init, una sola vez. Al girar el celular no se repite.
class DrinkListViewModel : ViewModel() {

    // Se crea acá porque no usamos inyección de dependencias (Hilt).
    // En una app más grande se recibiría por constructor.
    private val repository = CocktailRepository()

    // [Bato] _state privado y mutable; state público y de solo lectura (backing property).
    //   Así solo el ViewModel cambia el estado; la pantalla solo lo observa.
    private val _state = MutableStateFlow<UiState<DrinkListContent>>(UiState.Loading)
    val state: StateFlow<UiState<DrinkListContent>> = _state.asStateFlow()

    // [Bato] Ingredientes para el filtro. Si fallan, queda vacía y el filtro no muestra opciones.
    private val _ingredients = MutableStateFlow<List<String>>(emptyList())
    val ingredients: StateFlow<List<String>> = _ingredients.asStateFlow()

    private var lastSearch: DrinkSearch = DrinkSearch.ByName(DEFAULT_QUERY)
    private var searchJob: Job? = null

    init {
        search(lastSearch)
        loadIngredients()
    }

    fun searchByName(query: String) {
        val clean = query.trim()
        if (clean.isNotEmpty()) search(DrinkSearch.ByName(clean))
    }

    // [Bato] Búsqueda por ingrediente.
    fun filterByIngredient(ingredient: String) {
        search(DrinkSearch.ByIngredient(ingredient))
    }

    // [Mejora] Reintentar repite la última búsqueda (y los ingredientes, si no habían cargado).
    //   Antes: no había forma de reintentar; había que volver a escribir y buscar.
    fun retry() {
        search(lastSearch)
        if (_ingredients.value.isEmpty()) loadIngredients()
    }

    private fun search(search: DrinkSearch) {
        lastSearch = search
        // [Mejora] Cancela la búsqueda anterior si todavía no terminó.
        //   Por qué: si buscás dos cosas rápido, una respuesta vieja que llega tarde
        //   podría pisar a la nueva. Ni tu app ni la de Bato lo contemplaban.
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            _state.value = UiState.Loading
            _state.value = try {
                val drinks = when (search) {
                    is DrinkSearch.ByName -> repository.searchByName(search.query)
                    is DrinkSearch.ByIngredient -> repository.filterByIngredient(search.ingredient)
                }
                UiState.Success(DrinkListContent(search, drinks))
            } catch (e: CancellationException) {
                // [Mejora] La cancelación no es un error: se deja pasar.
                //   Antes (tu app y Bato): catch (e: Exception) también la atrapaba.
                throw e
            } catch (e: Exception) {
                e.toErrorState()
            }
        }
    }

    private fun loadIngredients() {
        viewModelScope.launch {
            try {
                _ingredients.value = repository.getIngredients()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                // Sin ingredientes el filtro queda vacío; la búsqueda por nombre sigue funcionando.
                // No se muestra un segundo mensaje de error: ya lo muestra la búsqueda principal.
            }
        }
    }

    companion object {
        const val DEFAULT_QUERY = "margarita"
    }
}

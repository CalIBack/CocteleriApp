package com.example.cocktailapp.data.repository

import com.example.cocktailapp.data.model.Drink
import com.example.cocktailapp.data.network.CocktailApi
import com.example.cocktailapp.data.network.RetrofitClient

// [Bato] Repositorio: la única puerta de entrada a los datos.
//   Antes: los dos Fragments llamaban directo a RetrofitClient.api.
//   Por qué: si mañana se agrega una caché o se cambia de API, se toca solo esta clase
//   y las pantallas no se enteran.
//
// [Mejora] Diferencias con el repositorio de Bato:
//   1. Recibe la API por constructor (con RetrofitClient.api por defecto).
//      Bato la creaba adentro, así que no se podía reemplazar. Acá, en un test,
//      se le puede pasar una API falsa.
//   2. Devuelve datos ya limpios (List<Drink>, Drink?, List<String>) en vez de
//      Response<...>. Los "null" de la API se resuelven acá, una sola vez,
//      y los ViewModels no tienen que repetir esa lógica.
class CocktailRepository(
    private val api: CocktailApi = RetrofitClient.api
) {

    // Si la API no encuentra nada manda "drinks": null; orEmpty() lo convierte en lista vacía.
    suspend fun searchByName(query: String): List<Drink> =
        api.searchByName(query).drinks.orEmpty()

    // [Bato] Búsqueda por ingrediente.
    suspend fun filterByIngredient(ingredient: String): List<Drink> =
        api.filterByIngredient(ingredient).drinks.orEmpty()

    // lookup.php devuelve una lista de un elemento: se toma ese, o null si no existe.
    suspend fun getById(id: String): Drink? =
        api.getById(id).drinks?.firstOrNull()

    // [Bato] Ingredientes para el filtro, ordenados alfabéticamente.
    //   Diferencia con Bato: él agregaba "Selecciona un ingrediente" a la lista desde
    //   el ViewModel, y después comparaba ese mismo texto en la Activity para ignorarlo.
    //   Acá la lista tiene solo datos reales; el texto de ayuda va en el XML (hint).
    suspend fun getIngredients(): List<String> =
        api.listIngredients().drinks.orEmpty()
            .mapNotNull { it.name?.trim()?.takeIf { name -> name.isNotEmpty() } }
            .distinctBy { it.lowercase() }
            .sortedBy { it.lowercase() }
}

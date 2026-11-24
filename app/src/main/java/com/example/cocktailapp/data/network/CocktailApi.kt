package com.example.cocktailapp.data.network

import com.example.cocktailapp.data.model.DrinkResponse
import com.example.cocktailapp.data.model.IngredientResponse
import retrofit2.http.GET
import retrofit2.http.Query

// [Bato] Paquete propio para la red (data/network).
//   Antes: estaba en el paquete "data" junto con los modelos.
//   Por qué: separar responsabilidades; acá solo vive lo que habla con internet.
interface CocktailApi {

    // Buscar por nombre: search.php?s=margarita
    @GET("search.php")
    suspend fun searchByName(@Query("s") name: String): DrinkResponse

    // Detalle completo por ID: lookup.php?i=11007
    @GET("lookup.php")
    suspend fun getById(@Query("i") id: String): DrinkResponse

    // [Bato] Filtrar por ingrediente: filter.php?i=Vodka
    //   Antes: no existía.
    //   Por qué: permite encontrar cócteles por lo que tenés en casa.
    //   Ojo: esta respuesta solo trae id, nombre y foto (el resto llega en null).
    @GET("filter.php")
    suspend fun filterByIngredient(@Query("i") ingredient: String): DrinkResponse

    // [Bato] Lista de todos los ingredientes para el filtro: list.php?i=list
    //   Antes: no existía.
    //   Por qué: llena el menú desplegable con ingredientes que la API conoce,
    //   así el usuario no puede pedir uno que no existe.
    @GET("list.php?i=list")
    suspend fun listIngredients(): IngredientResponse

    // Nota: Bato hacía que cada función devolviera Response<...> y revisaba
    // response.isSuccessful a mano en cada llamada. Acá se dejó el tipo directo:
    // si el servidor responde con error, Retrofit lanza HttpException, y se la
    // traduce a un mensaje en un solo lugar (ui/common/ErrorMessages.kt).
    // Es la misma distinción de errores que hacía Bato, con menos código repetido.
}

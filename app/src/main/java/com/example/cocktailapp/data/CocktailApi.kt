package com.example.cocktailapp.data

import retrofit2.http.GET
import retrofit2.http.Query

interface CocktailApi {

    // Buscar por nombre: search.php?s=margarita
    @GET("search.php")
    suspend fun searchByName(@Query("s") name: String): DrinkResponse

    // Detalle completo por ID: lookup.php?i=11007
    @GET("lookup.php")
    suspend fun getById(@Query("i") id: String): DrinkResponse
}
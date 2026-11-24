package com.example.cocktailapp.datos.red

import com.example.cocktailapp.datos.modelo.RespuestaCocteles
import com.example.cocktailapp.datos.modelo.RespuestaIngredientes
import retrofit2.http.GET
import retrofit2.http.Query

interface CoctelApi {

    @GET("search.php")
    suspend fun buscarPorNombre(@Query("s") nombre: String): RespuestaCocteles

    @GET("lookup.php")
    suspend fun obtenerPorId(@Query("i") id: String): RespuestaCocteles

    // Solo trae id, nombre y foto
    @GET("filter.php")
    suspend fun filtrarPorIngrediente(@Query("i") ingrediente: String): RespuestaCocteles

    @GET("list.php?i=list")
    suspend fun listarIngredientes(): RespuestaIngredientes
}

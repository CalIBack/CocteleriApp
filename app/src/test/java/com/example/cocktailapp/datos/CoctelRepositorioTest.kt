package com.example.cocktailapp.datos

import com.example.cocktailapp.datos.modelo.RespuestaCocteles
import com.example.cocktailapp.datos.modelo.RespuestaIngredientes
import com.example.cocktailapp.datos.red.CoctelApi
import com.example.cocktailapp.datos.repositorio.CoctelRepositorio
import com.google.gson.Gson
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class CoctelRepositorioTest {

    // API falsa que responde JSON fijo, sin internet
    private class ApiFalsa(
        private val jsonCocteles: String = """{"drinks":null}""",
        private val jsonIngredientes: String = """{"drinks":null}"""
    ) : CoctelApi {
        private val gson = Gson()
        private fun cocteles() = gson.fromJson(jsonCocteles, RespuestaCocteles::class.java)
        override suspend fun buscarPorNombre(nombre: String) = cocteles()
        override suspend fun obtenerPorId(id: String) = cocteles()
        override suspend fun filtrarPorIngrediente(ingrediente: String) = cocteles()
        override suspend fun listarIngredientes(): RespuestaIngredientes =
            gson.fromJson(jsonIngredientes, RespuestaIngredientes::class.java)
    }

    @Test
    fun sinResultadosDevuelveListaVacia() = runBlocking {
        val repositorio = CoctelRepositorio(ApiFalsa())
        assertTrue(repositorio.buscarPorNombre("zzzz").isEmpty())
        assertTrue(repositorio.filtrarPorIngrediente("zzzz").isEmpty())
    }

    @Test
    fun obtenerPorIdDevuelveElPrimeroONull() = runBlocking {
        val conCoctel = CoctelRepositorio(ApiFalsa("""{"drinks":[{"idDrink":"11007","strDrink":"Margarita"}]}"""))
        assertEquals("Margarita", conCoctel.obtenerPorId("11007")?.nombre)
        assertNull(CoctelRepositorio(ApiFalsa()).obtenerPorId("0"))
    }

    @Test
    fun ingredientesLimpiosSinRepetirYOrdenados() = runBlocking {
        val repositorio = CoctelRepositorio(
            ApiFalsa(
                jsonIngredientes = """{"drinks":[
                    {"strIngredient1":"vodka"},
                    {"strIngredient1":" Gin "},
                    {"strIngredient1":""},
                    {"strIngredient1":null},
                    {"strIngredient1":"Vodka"},
                    {"strIngredient1":"Amaretto"}
                ]}"""
            )
        )
        assertEquals(listOf("Amaretto", "Gin", "vodka"), repositorio.obtenerIngredientes())
    }
}

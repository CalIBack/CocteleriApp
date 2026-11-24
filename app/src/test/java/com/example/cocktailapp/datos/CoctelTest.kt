package com.example.cocktailapp.datos

import com.example.cocktailapp.datos.modelo.Coctel
import com.example.cocktailapp.datos.modelo.RespuestaCocteles
import com.google.gson.Gson
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class CoctelTest {

    private val gson = Gson()

    private fun coctelDesdeJson(json: String): Coctel = gson.fromJson(json, Coctel::class.java)

    @Test
    fun ingredientesConMedidaSinMedidaYVacios() {
        val margarita = coctelDesdeJson(
            """
            {"idDrink":"11007","strDrink":"Margarita",
             "strIngredient1":"Tequila","strMeasure1":"1 1/2 oz ",
             "strIngredient2":" Triple sec ","strMeasure2":"1/2 oz",
             "strIngredient3":"Salt","strMeasure3":null,
             "strIngredient4":"","strMeasure4":"1 oz",
             "strIngredient5":null}
            """
        )

        assertEquals(
            listOf("1 1/2 oz Tequila", "1/2 oz Triple sec", "Salt"),
            margarita.obtenerIngredientes()
        )
    }

    @Test
    fun descripcionCortaConCategoriaYAlcohol() {
        val coctel = coctelDesdeJson("""{"idDrink":"1","strDrink":"A","strCategory":"Cocktail","strAlcoholic":"Alcoholic"}""")
        assertEquals("Cocktail · Alcoholic", coctel.descripcionCorta())
    }

    @Test
    fun descripcionCortaNullSinDatos() {
        val coctel = coctelDesdeJson("""{"idDrink":"1","strDrink":"A","strDrinkThumb":"url"}""")
        assertNull(coctel.descripcionCorta())
    }

    @Test
    fun sinResultadosLaListaEsNull() {
        val respuesta = gson.fromJson("""{"drinks":null}""", RespuestaCocteles::class.java)
        assertNull(respuesta.cocteles)
    }
}

package com.example.cocktailapp.data.model

import com.google.gson.Gson
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

// [Mejora] Pruebas unitarias reales.
//   Antes: solo existía ExampleUnitTest (la prueba de ejemplo "2 + 2 = 4" que crea Android Studio).
//   Por qué: la lógica que no depende de Android (modelo y repositorio) se puede probar en la PC,
//   en milisegundos, sin emulador. Se corren con el botón ▶ al lado de cada clase de prueba.
class DrinkTest {

    private val gson = Gson()

    // Se arma el Drink desde JSON, como lo hace Retrofit; así también se prueban los @SerializedName.
    private fun drink(json: String): Drink = gson.fromJson(json, Drink::class.java)

    @Test
    fun ingredientesConMedidaSinMedidaYVacios() {
        val margarita = drink(
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
            margarita.getIngredientsList()
        )
    }

    @Test
    fun descripcionCortaConCategoriaYTipo() {
        val d = drink("""{"idDrink":"1","strDrink":"A","strCategory":"Cocktail","strAlcoholic":"Alcoholic"}""")
        assertEquals("Cocktail · Alcoholic", d.shortDescription())
    }

    @Test
    fun descripcionCortaEsNullSinDatos() {
        // filter.php (búsqueda por ingrediente) no trae categoría ni tipo.
        val d = drink("""{"idDrink":"1","strDrink":"A","strDrinkThumb":"url"}""")
        assertNull(d.shortDescription())
    }

    @Test
    fun respuestaSinResultadosTraeDrinksNull() {
        val response = gson.fromJson("""{"drinks":null}""", DrinkResponse::class.java)
        assertNull(response.drinks)
    }
}

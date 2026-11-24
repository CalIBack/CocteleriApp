package com.example.cocktailapp.data.repository

import com.example.cocktailapp.data.model.DrinkResponse
import com.example.cocktailapp.data.model.IngredientResponse
import com.example.cocktailapp.data.network.CocktailApi
import com.google.gson.Gson
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

// [Mejora] Se puede probar el repositorio porque recibe la API por constructor:
// acá se le pasa una API falsa que responde JSON fijo, sin internet.
class CocktailRepositoryTest {

    private val gson = Gson()

    private class FakeApi(
        private val drinksJson: String = """{"drinks":null}""",
        private val ingredientsJson: String = """{"drinks":null}"""
    ) : CocktailApi {
        private val gson = Gson()
        private fun drinks() = gson.fromJson(drinksJson, DrinkResponse::class.java)
        override suspend fun searchByName(name: String) = drinks()
        override suspend fun getById(id: String) = drinks()
        override suspend fun filterByIngredient(ingredient: String) = drinks()
        override suspend fun listIngredients(): IngredientResponse =
            gson.fromJson(ingredientsJson, IngredientResponse::class.java)
    }

    @Test
    fun sinResultadosDevuelveListaVacia() = runBlocking {
        val repository = CocktailRepository(FakeApi())
        assertTrue(repository.searchByName("zzzz").isEmpty())
        assertTrue(repository.filterByIngredient("zzzz").isEmpty())
    }

    @Test
    fun getByIdDevuelveElPrimeroONull() = runBlocking {
        val conCoctel = CocktailRepository(
            FakeApi("""{"drinks":[{"idDrink":"11007","strDrink":"Margarita"}]}""")
        )
        assertEquals("Margarita", conCoctel.getById("11007")?.name)
        assertNull(CocktailRepository(FakeApi()).getById("0"))
    }

    @Test
    fun ingredientesLimpiosSinRepetirYOrdenados() = runBlocking {
        val repository = CocktailRepository(
            FakeApi(
                ingredientsJson = """{"drinks":[
                    {"strIngredient1":"vodka"},
                    {"strIngredient1":" Gin "},
                    {"strIngredient1":""},
                    {"strIngredient1":null},
                    {"strIngredient1":"Vodka"},
                    {"strIngredient1":"Amaretto"}
                ]}"""
            )
        )
        assertEquals(listOf("Amaretto", "Gin", "vodka"), repository.getIngredients())
    }
}

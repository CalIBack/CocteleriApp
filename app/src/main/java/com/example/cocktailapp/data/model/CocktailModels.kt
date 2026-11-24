package com.example.cocktailapp.data.model

import com.google.gson.annotations.SerializedName

// [Bato] Paquete propio para los modelos (data/model).
//   Antes: los modelos estaban junto a Retrofit en un único paquete "data".
//   Por qué: cada paquete tiene una sola responsabilidad (modelos, red, repositorio),
//   así es más fácil encontrar las cosas y explicar la arquitectura.

data class DrinkResponse(
    // [Bato] @SerializedName también en "drinks".
    //   Antes: era el único campo sin anotación.
    //   Por qué: si algún día se activa la minificación (R8), los nombres de los campos
    //   se acortan y Gson dejaría de encontrarlos. Con la anotación queda protegido.
    @SerializedName("drinks") val drinks: List<Drink>?
)

data class Drink(
    @SerializedName("idDrink") val id: String,
    @SerializedName("strDrink") val name: String,
    @SerializedName("strDrinkThumb") val thumbnail: String?,
    @SerializedName("strInstructions") val instructions: String?,

    // [Mejora] Categoría, tipo y vaso, para mostrarlos como etiquetas en el detalle.
    //   Antes: no se leían, aunque la API ya los mandaba.
    //   Por qué: suman información útil sin pedir nada extra a la API.
    //   Son nullable porque filter.php (búsqueda por ingrediente) no los manda.
    @SerializedName("strCategory") val category: String?,
    @SerializedName("strAlcoholic") val alcoholic: String?,
    @SerializedName("strGlass") val glass: String?,

    @SerializedName("strIngredient1") val ingredient1: String?,
    @SerializedName("strIngredient2") val ingredient2: String?,
    @SerializedName("strIngredient3") val ingredient3: String?,
    @SerializedName("strIngredient4") val ingredient4: String?,
    @SerializedName("strIngredient5") val ingredient5: String?,
    @SerializedName("strIngredient6") val ingredient6: String?,
    @SerializedName("strIngredient7") val ingredient7: String?,
    @SerializedName("strIngredient8") val ingredient8: String?,
    @SerializedName("strIngredient9") val ingredient9: String?,
    @SerializedName("strIngredient10") val ingredient10: String?,
    @SerializedName("strIngredient11") val ingredient11: String?,
    @SerializedName("strIngredient12") val ingredient12: String?,
    @SerializedName("strIngredient13") val ingredient13: String?,
    @SerializedName("strIngredient14") val ingredient14: String?,
    @SerializedName("strIngredient15") val ingredient15: String?,

    @SerializedName("strMeasure1") val measure1: String?,
    @SerializedName("strMeasure2") val measure2: String?,
    @SerializedName("strMeasure3") val measure3: String?,
    @SerializedName("strMeasure4") val measure4: String?,
    @SerializedName("strMeasure5") val measure5: String?,
    @SerializedName("strMeasure6") val measure6: String?,
    @SerializedName("strMeasure7") val measure7: String?,
    @SerializedName("strMeasure8") val measure8: String?,
    @SerializedName("strMeasure9") val measure9: String?,
    @SerializedName("strMeasure10") val measure10: String?,
    @SerializedName("strMeasure11") val measure11: String?,
    @SerializedName("strMeasure12") val measure12: String?,
    @SerializedName("strMeasure13") val measure13: String?,
    @SerializedName("strMeasure14") val measure14: String?,
    @SerializedName("strMeasure15") val measure15: String?
) {
    // Junta ingrediente + medida en una lista de texto, ignorando los vacíos.
    // Se mantiene acá (en el modelo) y no en el Fragment, como hacía Bato:
    // así la puede usar cualquier pantalla y se puede probar sin Android.
    fun getIngredientsList(): List<String> {
        val ingredients = listOf(
            ingredient1 to measure1, ingredient2 to measure2, ingredient3 to measure3,
            ingredient4 to measure4, ingredient5 to measure5, ingredient6 to measure6,
            ingredient7 to measure7, ingredient8 to measure8, ingredient9 to measure9,
            ingredient10 to measure10, ingredient11 to measure11, ingredient12 to measure12,
            ingredient13 to measure13, ingredient14 to measure14, ingredient15 to measure15
        )
        // [Mejora] mapNotNull en vez de filter + map con "ingredient!!".
        //   Antes: .filter { !it.first.isNullOrBlank() }.map { ... ingredient!! ... }
        //   Por qué: evita el operador !! (que puede cerrar la app si algo cambia)
        //   y también limpia espacios sobrantes del nombre del ingrediente.
        return ingredients.mapNotNull { (ingredient, measure) ->
            val name = ingredient?.trim().orEmpty()
            when {
                name.isEmpty() -> null
                measure.isNullOrBlank() -> name
                else -> "${measure.trim()} $name"
            }
        }
    }

    // [Mejora] Texto corto para la lista, por ejemplo "Cocktail · Alcoholic".
    //   Antes: la fila solo mostraba el nombre.
    //   Por qué: da contexto sin abrir el detalle. Devuelve null si no hay datos,
    //   y la vista se oculta.
    fun shortDescription(): String? =
        listOfNotNull(category, alcoholic)
            .filter { it.isNotBlank() }
            .joinToString(" · ")
            .ifBlank { null }
}

// [Bato] Lista de ingredientes para el filtro (endpoint list.php?i=list).
//   Antes: tu app no tenía filtro por ingrediente.
//   Por qué: es la funcionalidad extra que tenía la app de Bato.
//   Diferencia con Bato: él reutilizaba el modelo Cocktail para esta respuesta, que solo
//   trae "strIngredient1". Los campos obligatorios (id, nombre) quedaban en null aunque
//   estaban declarados como no nulos: una falla esperando a pasar. Acá tiene su propio modelo.
data class IngredientResponse(
    @SerializedName("drinks") val drinks: List<Ingredient>?
)

data class Ingredient(
    @SerializedName("strIngredient1") val name: String?
)

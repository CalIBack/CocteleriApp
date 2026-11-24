package com.example.cocktailapp.datos.modelo

import com.google.gson.annotations.SerializedName

data class RespuestaCocteles(
    @SerializedName("drinks") val cocteles: List<Coctel>?
)

data class Coctel(
    @SerializedName("idDrink") val id: String,
    @SerializedName("strDrink") val nombre: String,
    @SerializedName("strDrinkThumb") val miniatura: String?,
    @SerializedName("strInstructions") val instrucciones: String?,
    @SerializedName("strCategory") val categoria: String?,
    @SerializedName("strAlcoholic") val alcohol: String?,
    @SerializedName("strGlass") val vaso: String?,

    // La API manda 15 ingredientes y 15 medidas como campos sueltos, no como lista
    @SerializedName("strIngredient1") val ingrediente1: String?,
    @SerializedName("strIngredient2") val ingrediente2: String?,
    @SerializedName("strIngredient3") val ingrediente3: String?,
    @SerializedName("strIngredient4") val ingrediente4: String?,
    @SerializedName("strIngredient5") val ingrediente5: String?,
    @SerializedName("strIngredient6") val ingrediente6: String?,
    @SerializedName("strIngredient7") val ingrediente7: String?,
    @SerializedName("strIngredient8") val ingrediente8: String?,
    @SerializedName("strIngredient9") val ingrediente9: String?,
    @SerializedName("strIngredient10") val ingrediente10: String?,
    @SerializedName("strIngredient11") val ingrediente11: String?,
    @SerializedName("strIngredient12") val ingrediente12: String?,
    @SerializedName("strIngredient13") val ingrediente13: String?,
    @SerializedName("strIngredient14") val ingrediente14: String?,
    @SerializedName("strIngredient15") val ingrediente15: String?,

    @SerializedName("strMeasure1") val medida1: String?,
    @SerializedName("strMeasure2") val medida2: String?,
    @SerializedName("strMeasure3") val medida3: String?,
    @SerializedName("strMeasure4") val medida4: String?,
    @SerializedName("strMeasure5") val medida5: String?,
    @SerializedName("strMeasure6") val medida6: String?,
    @SerializedName("strMeasure7") val medida7: String?,
    @SerializedName("strMeasure8") val medida8: String?,
    @SerializedName("strMeasure9") val medida9: String?,
    @SerializedName("strMeasure10") val medida10: String?,
    @SerializedName("strMeasure11") val medida11: String?,
    @SerializedName("strMeasure12") val medida12: String?,
    @SerializedName("strMeasure13") val medida13: String?,
    @SerializedName("strMeasure14") val medida14: String?,
    @SerializedName("strMeasure15") val medida15: String?
) {
    fun obtenerIngredientes(): List<String> {
        val pares = listOf(
            ingrediente1 to medida1, ingrediente2 to medida2, ingrediente3 to medida3,
            ingrediente4 to medida4, ingrediente5 to medida5, ingrediente6 to medida6,
            ingrediente7 to medida7, ingrediente8 to medida8, ingrediente9 to medida9,
            ingrediente10 to medida10, ingrediente11 to medida11, ingrediente12 to medida12,
            ingrediente13 to medida13, ingrediente14 to medida14, ingrediente15 to medida15
        )

        // Arma "1 oz Tequila" y descarta los ingredientes vacíos
        return pares.mapNotNull { (ingrediente, medida) ->
            val nombre = ingrediente?.trim().orEmpty()
            when {
                nombre.isEmpty() -> null
                medida.isNullOrBlank() -> nombre
                else -> "${medida.trim()} $nombre"
            }
        }
    }

    // "Cocktail · Alcoholic", o null si no hay datos
    fun descripcionCorta(): String? =
        listOfNotNull(categoria, alcohol)
            .filter { it.isNotBlank() }
            .joinToString(" · ")
            .ifBlank { null }
}

data class RespuestaIngredientes(
    @SerializedName("drinks") val ingredientes: List<Ingrediente>?
)

data class Ingrediente(
    @SerializedName("strIngredient1") val nombre: String?
)

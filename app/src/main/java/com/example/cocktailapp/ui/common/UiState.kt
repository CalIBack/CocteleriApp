package com.example.cocktailapp.ui.common

// [Mejora] Un solo estado por pantalla, con tres casos posibles.
//   Antes (tu app): el Fragment prendía y apagaba el spinner y la lista a mano.
//   Antes (Bato): tres LiveData separados (lista, cargando, error). Se pueden
//   contradecir, por ejemplo "cargando" y "error" a la vez.
//   Por qué: con una sealed interface la pantalla siempre está en UN estado,
//   y el "when" obliga a dibujar los tres casos.
sealed interface UiState<out T> {
    data object Loading : UiState<Nothing>
    data class Success<T>(val data: T) : UiState<T>

    // El mensaje es un id de strings.xml (R.string.algo), no un texto fijo:
    // el ViewModel no escribe textos de pantalla y se pueden traducir.
    // formatArg: dato opcional para el texto (por ejemplo, el código de error HTTP).
    // canRetry: si tiene sentido mostrar el botón "Reintentar".
    data class Error(
        val messageRes: Int,
        val formatArg: Int? = null,
        val canRetry: Boolean = true
    ) : UiState<Nothing>
}

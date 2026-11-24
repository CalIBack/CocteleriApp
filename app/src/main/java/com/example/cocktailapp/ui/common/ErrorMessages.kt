package com.example.cocktailapp.ui.common

import android.content.Context
import com.example.cocktailapp.R
import com.google.gson.JsonParseException
import retrofit2.HttpException
import java.io.IOException

// [Bato] Distinguir el tipo de error.
//   Antes: cualquier falla mostraba "Error de conexión", incluso si el problema
//   era del servidor (por ejemplo, un error 500).
//   Por qué: Bato separaba "Error en el servidor: código" de "Error de conexión".
//   Acá se hace lo mismo, en un único lugar para toda la app:
//     - IOException       → no hay internet o se cortó la conexión.
//     - HttpException     → el servidor respondió, pero con un código de error.
//     - JsonParseException → llegó algo que no tiene el formato esperado.
fun Throwable.toErrorState(): UiState.Error = when (this) {
    is IOException -> UiState.Error(R.string.error_no_connection)
    is HttpException -> UiState.Error(R.string.error_server, formatArg = code())
    is JsonParseException -> UiState.Error(R.string.error_bad_response)
    else -> UiState.Error(R.string.error_unexpected)
}

// Convierte el error en el texto final, con el dato opcional si lo hay.
fun UiState.Error.message(context: Context): String =
    if (formatArg != null) context.getString(messageRes, formatArg)
    else context.getString(messageRes)

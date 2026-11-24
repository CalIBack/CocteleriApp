package com.example.cocktailapp.ui.comun

import android.content.Context
import com.example.cocktailapp.R
import com.google.gson.JsonParseException
import retrofit2.HttpException
import java.io.IOException

fun Throwable.aEstadoError(): EstadoUi.Error = when (this) {
    is IOException -> EstadoUi.Error(R.string.error_sin_conexion)
    is HttpException -> EstadoUi.Error(R.string.error_servidor, argumento = code())
    is JsonParseException -> EstadoUi.Error(R.string.error_respuesta)
    else -> EstadoUi.Error(R.string.error_inesperado)
}

fun EstadoUi.Error.texto(context: Context): String =
    if (argumento != null) context.getString(idMensaje, argumento)
    else context.getString(idMensaje)

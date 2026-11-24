package com.example.cocktailapp.ui.comun

sealed interface EstadoUi<out T> {
    data object Cargando : EstadoUi<Nothing>
    data class Exito<T>(val datos: T) : EstadoUi<T>

    // idMensaje es un R.string; argumento completa el texto (ej. código HTTP)
    data class Error(
        val idMensaje: Int,
        val argumento: Int? = null,
        val sePuedeReintentar: Boolean = true
    ) : EstadoUi<Nothing>
}

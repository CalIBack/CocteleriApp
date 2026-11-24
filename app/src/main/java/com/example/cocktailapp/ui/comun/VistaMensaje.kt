package com.example.cocktailapp.ui.comun

import androidx.annotation.DrawableRes
import androidx.core.view.isVisible
import com.example.cocktailapp.databinding.ViewMensajeBinding

fun ViewMensajeBinding.mostrar(
    @DrawableRes icono: Int,
    texto: String,
    alReintentar: (() -> Unit)? = null
) {
    root.isVisible = true
    imagenMensaje.setImageResource(icono)
    textoMensaje.text = texto
    botonReintentar.isVisible = alReintentar != null
    botonReintentar.setOnClickListener { alReintentar?.invoke() }
}

fun ViewMensajeBinding.ocultar() {
    root.isVisible = false
}

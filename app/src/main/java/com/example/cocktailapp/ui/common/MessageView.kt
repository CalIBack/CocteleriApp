package com.example.cocktailapp.ui.common

import androidx.annotation.DrawableRes
import androidx.core.view.isVisible
import com.example.cocktailapp.databinding.ViewMessageBinding

// [Mejora] Vista reutilizable para "sin resultados" y "error" (layout/view_message.xml).
//   Antes: los mensajes eran Toasts que desaparecían a los segundos.
//   Antes (Bato): también Toasts, y como venían de un LiveData se repetían al girar el celular.
//   Por qué: un mensaje dentro de la pantalla queda visible, no se repite y puede
//   tener un botón "Reintentar". Las dos pantallas usan este mismo layout con <include>.
fun ViewMessageBinding.show(
    @DrawableRes icon: Int,
    text: String,
    onRetry: (() -> Unit)? = null
) {
    root.isVisible = true
    imageMessage.setImageResource(icon)
    textMessage.text = text
    buttonRetry.isVisible = onRetry != null
    buttonRetry.setOnClickListener { onRetry?.invoke() }
}

fun ViewMessageBinding.hide() {
    root.isVisible = false
}

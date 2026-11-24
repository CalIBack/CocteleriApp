package com.example.cocktailapp.ui.lista

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.cocktailapp.R
import com.example.cocktailapp.databinding.ItemCoctelBinding
import com.example.cocktailapp.datos.modelo.Coctel

class CoctelAdapter(
    private val alTocarCoctel: (Coctel) -> Unit
) : ListAdapter<Coctel, CoctelAdapter.CoctelViewHolder>(ComparadorCocteles) {

    class CoctelViewHolder(val binding: ItemCoctelBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CoctelViewHolder {
        val binding = ItemCoctelBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CoctelViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CoctelViewHolder, position: Int) {
        val coctel = getItem(position)

        with(holder.binding) {
            textoNombre.text = coctel.nombre

            val descripcion = coctel.descripcionCorta()
            textoDescripcion.text = descripcion
            textoDescripcion.isVisible = descripcion != null

            Glide.with(imagenMiniatura)
                .load(coctel.miniatura)
                .transform(CenterCrop(), RoundedCorners(24))
                .placeholder(R.drawable.ic_local_bar)
                .error(R.drawable.ic_local_bar)
                .into(imagenMiniatura)

            root.setOnClickListener { alTocarCoctel(coctel) }
        }
    }

    // ListAdapter lo usa para redibujar solo las filas que cambiaron
    private object ComparadorCocteles : DiffUtil.ItemCallback<Coctel>() {
        override fun areItemsTheSame(viejo: Coctel, nuevo: Coctel) = viejo.id == nuevo.id
        override fun areContentsTheSame(viejo: Coctel, nuevo: Coctel) = viejo == nuevo
    }
}

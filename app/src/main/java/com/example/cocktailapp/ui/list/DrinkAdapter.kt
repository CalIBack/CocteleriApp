package com.example.cocktailapp.ui.list

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
import com.example.cocktailapp.data.model.Drink
import com.example.cocktailapp.databinding.DrinkItemBinding

// [Mejora] ListAdapter + DiffUtil.
//   Antes (tu app y Bato): RecyclerView.Adapter con una lista propia y notifyDataSetChanged(),
//   que redibuja TODAS las filas aunque cambie una sola.
//   Por qué: ListAdapter compara la lista vieja con la nueva (DiffUtil) y redibuja solo
//   lo que cambió, con animación. Además ya guarda la lista: no hace falta "updateList".
class DrinkAdapter(
    private val onItemClick: (Drink) -> Unit
) : ListAdapter<Drink, DrinkAdapter.DrinkViewHolder>(DrinkDiff) {

    // [Bato] ViewBinding en el ViewHolder.
    //   Antes: view.findViewById(R.id.imageThumbnail) y view.findViewById(R.id.textName).
    //   Por qué: DrinkItemBinding se genera solo a partir de drink_item.xml, con cada vista
    //   ya tipada. No se puede escribir mal un id ni equivocarse de tipo.
    class DrinkViewHolder(val binding: DrinkItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DrinkViewHolder {
        val binding = DrinkItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DrinkViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DrinkViewHolder, position: Int) {
        val drink = getItem(position)
        with(holder.binding) {
            textName.text = drink.name

            // [Mejora] Subtítulo con categoría y tipo; se oculta si no hay datos
            // (la búsqueda por ingrediente no los trae).
            val description = drink.shortDescription()
            textDescription.text = description
            textDescription.isVisible = description != null

            Glide.with(imageThumbnail)
                .load(drink.thumbnail)
                // [Mejora] CenterCrop + RoundedCorners juntos.
                //   Antes: solo RoundedCorners, que reemplaza al scaleType del XML y podía
                //   dejar fotos no cuadradas deformadas o con bordes.
                .transform(CenterCrop(), RoundedCorners(24))
                .placeholder(R.drawable.ic_local_bar)
                .error(R.drawable.ic_local_bar)
                .into(imageThumbnail)

            root.setOnClickListener { onItemClick(drink) }
        }
    }

    // DiffUtil: cómo saber si dos filas son el mismo cóctel y si cambió algo.
    private object DrinkDiff : DiffUtil.ItemCallback<Drink>() {
        override fun areItemsTheSame(oldItem: Drink, newItem: Drink) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Drink, newItem: Drink) = oldItem == newItem
    }
}

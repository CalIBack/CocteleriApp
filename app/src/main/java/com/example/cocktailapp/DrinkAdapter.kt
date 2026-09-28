package com.example.cocktailapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.cocktailapp.data.Drink

class DrinkAdapter(
    private var drinks: List<Drink>,
    private val onItemClick: (Drink) -> Unit
) : RecyclerView.Adapter<DrinkAdapter.DrinkViewHolder>() {

    class DrinkViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.imageThumbnail)
        val name: TextView = view.findViewById(R.id.textName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DrinkViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.drink_item, parent, false)
        return DrinkViewHolder(view)
    }

    override fun onBindViewHolder(holder: DrinkViewHolder, position: Int) {
        val drink = drinks[position]
        holder.name.text = drink.name

        Glide.with(holder.image.context)
            .load(drink.thumbnail)
            .transform(RoundedCorners(40))
            .placeholder(android.R.drawable.ic_menu_gallery)
            .error(android.R.drawable.ic_menu_close_clear_cancel)
            .into(holder.image)

        holder.itemView.setOnClickListener {
            onItemClick(drink)
        }
    }

    override fun getItemCount(): Int = drinks.size

    fun updateList(newDrinks: List<Drink>) {
        drinks = newDrinks
        notifyDataSetChanged()
    }
}
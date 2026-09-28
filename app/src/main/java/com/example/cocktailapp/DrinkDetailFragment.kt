package com.example.cocktailapp

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.cocktailapp.data.RetrofitClient
import kotlinx.coroutines.launch

class DrinkDetailFragment : Fragment(R.layout.fragment_drink_detail) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val drinkId = arguments?.getString("drinkId")

        val scroll: View = view.findViewById(R.id.scrollDetail)
        val progressBar: ProgressBar = view.findViewById(R.id.progressBarDetail)
        val image: ImageView = view.findViewById(R.id.imageDetail)
        val name: TextView = view.findViewById(R.id.textDetailName)
        val ingredients: TextView = view.findViewById(R.id.textIngredients)
        val instructions: TextView = view.findViewById(R.id.textInstructions)

        scroll.visibility = View.INVISIBLE

        if (drinkId == null) {
            progressBar.visibility = View.GONE
            Toast.makeText(requireContext(), "No se pudo cargar el cóctel", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            try {
                val response = RetrofitClient.api.getById(drinkId)
                val drink = response.drinks?.firstOrNull()

                if (drink == null) {
                    Toast.makeText(requireContext(), "No se encontró el cóctel", Toast.LENGTH_SHORT).show()
                    return@launch
                }

                name.text = drink.name
                instructions.text = drink.instructions ?: "Sin instrucciones"

                val ingredientsText = drink.getIngredientsList()
                    .joinToString("\n") { "• $it" }
                ingredients.text = ingredientsText

                Glide.with(this@DrinkDetailFragment)
                    .load(drink.thumbnail)
                    .transform(RoundedCorners(80))
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .into(image)

            } catch (e: Exception) {
                Log.e("CocktailApp", "Error al cargar detalle", e)
                Toast.makeText(requireContext(), "Error de conexión", Toast.LENGTH_SHORT).show()
            } finally {
                progressBar.visibility = View.GONE
                scroll.visibility = View.VISIBLE
            }
        }
    }
}
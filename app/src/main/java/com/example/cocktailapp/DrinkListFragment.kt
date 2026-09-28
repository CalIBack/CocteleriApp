package com.example.cocktailapp

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cocktailapp.data.RetrofitClient
import kotlinx.coroutines.launch

class DrinkListFragment : Fragment(R.layout.fragment_drink_list) {

    private lateinit var recyclerView: RecyclerView
    private lateinit var searchBox: EditText
    private lateinit var progressBar: ProgressBar
    private lateinit var adapter: DrinkAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerDrinks)
        searchBox = view.findViewById(R.id.editSearch)
        progressBar = view.findViewById(R.id.progressBarList)

        adapter = DrinkAdapter(emptyList()) { drink ->
            val bundle = Bundle().apply {
                putString("drinkId", drink.id)
            }
            findNavController().navigate(
                R.id.action_drinkList_to_drinkDetail,
                bundle
            )
        }

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        searchDrinks("margarita")

        searchBox.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val query = searchBox.text.toString().trim()
                if (query.isNotEmpty()) searchDrinks(query)
                true
            } else {
                false
            }
        }
    }

    private fun searchDrinks(query: String) {
        progressBar.visibility = View.VISIBLE
        recyclerView.visibility = View.INVISIBLE

        lifecycleScope.launch {
            try {
                val response = RetrofitClient.api.searchByName(query)
                val results = response.drinks ?: emptyList()
                adapter.updateList(results)

                if (results.isEmpty()) {
                    Toast.makeText(requireContext(), "Sin resultados", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("CocktailApp", "Error al buscar", e)
                Toast.makeText(requireContext(), "Error de conexión", Toast.LENGTH_SHORT).show()
            } finally {
                progressBar.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
            }
        }
    }
}
package com.example.cocktailapp.datos.repositorio

import com.example.cocktailapp.datos.modelo.Coctel
import com.example.cocktailapp.datos.red.ClienteRetrofit
import com.example.cocktailapp.datos.red.CoctelApi

// Recibe la API por parámetro para poder probarlo con una API falsa
class CoctelRepositorio(
    private val api: CoctelApi = ClienteRetrofit.api
) {

    // Si no hay resultados la API manda "drinks": null
    suspend fun buscarPorNombre(nombre: String): List<Coctel> =
        api.buscarPorNombre(nombre).cocteles.orEmpty()

    suspend fun filtrarPorIngrediente(ingrediente: String): List<Coctel> =
        api.filtrarPorIngrediente(ingrediente).cocteles.orEmpty()

    suspend fun obtenerPorId(id: String): Coctel? =
        api.obtenerPorId(id).cocteles?.firstOrNull()

    suspend fun obtenerIngredientes(): List<String> =
        api.listarIngredientes().ingredientes.orEmpty()
            .mapNotNull { it.nombre?.trim()?.takeIf { nombre -> nombre.isNotEmpty() } }
            .distinctBy { it.lowercase() }
            .sortedBy { it.lowercase() }
}

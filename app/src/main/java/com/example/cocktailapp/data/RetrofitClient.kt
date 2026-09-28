package com.example.cocktailapp.data

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private const val BASE_URL = "https://www.thecocktaildb.com/api/json/v1/1/"

    val api: CocktailApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL) // debe terminar en "/"
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CocktailApi::class.java)
    }
}
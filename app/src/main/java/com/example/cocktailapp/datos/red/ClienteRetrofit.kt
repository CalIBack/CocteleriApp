package com.example.cocktailapp.datos.red

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ClienteRetrofit {

    private const val URL_BASE = "https://www.thecocktaildb.com/api/json/v1/1/"

    val api: CoctelApi by lazy {
        Retrofit.Builder()
            .baseUrl(URL_BASE)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CoctelApi::class.java)
    }
}

package net.hassan.newsapp.api

import net.hassan.newsapp.util.Constants.Companion.BASE_URL
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    private val retrofitInstance: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val newsApiInterface:NewsApiInterface by lazy {
        retrofitInstance
            .create(NewsApiInterface::class.java)
    }
}
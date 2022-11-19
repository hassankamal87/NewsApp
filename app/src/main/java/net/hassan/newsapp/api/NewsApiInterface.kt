package net.hassan.newsapp.api

import net.hassan.newsapp.util.Constants.Companion.API_KEY
import net.hassan.newsapp.models.Respon
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApiInterface {

    @GET("v2/top-headlines")
    suspend fun getBreakingNews(
        @Query("country")
        countryCode:String = "eg",
        @Query("page")
        pageNumber: Int = 1,
        @Query("apiKey")
        apiKey:String = API_KEY
    ):Response<Respon>

    @GET("v2/everything")
    suspend fun searchForNews(
        @Query("q")
        searchQuery:String,
        @Query("page")
        pageNumber: Int = 1,
        @Query("apiKey")
        apiKey:String = API_KEY
    ):Response<Respon>
}
package com.example.newsapp.api

import com.example.newsapp.models.NewsResponse
import com.example.newsapp.util.Constants.Companion.API_KEY
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsAPI {
    @GET("v2/top-headlines")
    suspend fun getHeadlines(
        @Query("country")
        countryCode:String="pt",
        @Query("page")
        pageNumber:Int=1,
        @Query("apiKey")
        apiKey:String=API_KEY
    ):Response<NewsResponse>

    @GET("v2/everything")
    suspend fun searchNews(
        @Query("q")
        query:String,
        @Query("page")
        pageNumber: Int=1,
        @Query("apiKey")
        apiKey:String=API_KEY
    ):Response<NewsResponse>

}
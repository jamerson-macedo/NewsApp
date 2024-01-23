package com.example.newsapp.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object HttpClient{
    private const val BASE_URL = "https://api.tiagoaguiar.co/jokerapp/"
    const val API_KEY = "40bf988aafb04ef49f9402e0a66f669a"
    private fun httpClient(): OkHttpClient {
        val interceptor = HttpLoggingInterceptor()
        // vendo onde vou 9interceptar
        interceptor.level = HttpLoggingInterceptor.Level.BODY

        return OkHttpClient.Builder().addInterceptor(interceptor).build()


    }

    fun retrofit() = Retrofit.Builder().baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create()).client(httpClient()).build()
}
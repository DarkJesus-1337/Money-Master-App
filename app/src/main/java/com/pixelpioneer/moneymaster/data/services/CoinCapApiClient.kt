package com.pixelpioneer.moneymaster.data.services

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object CoinCapApiClient {
    private const val API_KEY = "d43a6346d536e50273f6c87c078e88cacd4d7171fadfd58d4255b6cf7913b2ae"

    private val authInterceptor = Interceptor { chain ->
        val original: Request = chain.request()
        val request = original.newBuilder()
           .addHeader("Authorization", "Bearer $API_KEY")
            .build()
        chain.proceed(request)
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .build()

    val api: CoinCapApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://rest.coincap.io/v3/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CoinCapApiService::class.java)
    }
}
package com.pixelpioneer.moneymaster.core.network

import com.pixelpioneer.moneymaster.data.remote.api.CoinCapApiService
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Client for accessing the CoinCap API with authentication.
 *
 * Adds an authorization header using the API key from [RemoteConfigManager].
 * Provides a lazily initialized [CoinCapApiService] instance.
 *
 * @property remoteConfigManager Manager for retrieving API keys from remote config.
 */
class CoinCapApiClient(private val remoteConfigManager: RemoteConfigManager) {

    private val authInterceptor = Interceptor { chain ->
        val original: Request = chain.request()
        val apiKey = remoteConfigManager.getCoinCapApiKey()
        val request = original.newBuilder()
            .addHeader("Authorization", "Bearer $apiKey")
            .build()
        chain.proceed(request)
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .build()

    /**
     * Lazily initialized CoinCap API service.
     */
    val api: CoinCapApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://rest.coincap.io/v3/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CoinCapApiService::class.java)
    }
}
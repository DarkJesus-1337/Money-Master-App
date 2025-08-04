package com.pixelpioneer.moneymaster.core.network

import com.pixelpioneer.moneymaster.data.remote.dto.OcrSpaceApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Singleton client for accessing the OCR Space API.
 *
 * Provides a lazily initialized [OcrSpaceApiService] instance for image parsing.
 */
object OcrSpaceApiClient {
    private const val BASE_URL = "https://api.ocr.space/"

    /**
     * Lazily initialized OCR Space API service.
     */
    val apiService: OcrSpaceApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(OcrSpaceApiService::class.java)
    }
}
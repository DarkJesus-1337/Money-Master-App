package com.pixelpioneer.moneymaster.core.network

import com.pixelpioneer.moneymaster.data.remote.dto.OcrSpaceResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import kotlin.jvm.java

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

/**
 * Service interface for accessing OCR Space API endpoints.
 *
 * Provides a method to parse images and extract text using OCR.
 */
interface OcrSpaceApiService {
    /**
     * Parses an image using the OCR Space API.
     *
     * @param apiKey The API key for authentication.
     * @param language The language to use for OCR.
     * @param overlay Whether to include text overlay information.
     * @param orientation Whether to detect text orientation.
     * @param isTable Whether to detect tables in the image.
     * @param scale Whether to scale the image.
     * @param image The image file to be parsed.
     * @return A [Response] containing [OcrSpaceResponse] with parsed text and details.
     */
    @Multipart
    @POST("parse/image")
    suspend fun parseImage(
        @Part("apikey") apiKey: RequestBody,
        @Part("language") language: RequestBody,
        @Part("isOverlayRequired") overlay: RequestBody,
        @Part("detectOrientation") orientation: RequestBody,
        @Part("isTable") isTable: RequestBody,
        @Part("scale") scale: RequestBody,
        @Part image: MultipartBody.Part
    ): Response<OcrSpaceResponse>
}
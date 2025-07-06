package com.pixelpioneer.moneymaster.data.services

import com.pixelpioneer.moneymaster.data.model.AssetsResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface CoinCapApiService {
    @GET("assets")
    suspend fun getAssets(
        @Query("limit") limit: Int = 10
    ): Response<AssetsResponse>
}
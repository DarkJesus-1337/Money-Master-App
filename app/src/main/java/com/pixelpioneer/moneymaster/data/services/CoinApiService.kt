package com.pixelpioneer.moneymaster.data.services

import com.pixelpioneer.moneymaster.data.model.AssetsResponse
import com.pixelpioneer.moneymaster.data.model.HistoryResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface CoinCapApiService {
    @GET("assets")
    suspend fun getAssets(
        @Query("limit") limit: Int = 10
    ): Response<AssetsResponse>

    @GET("assets/{id}/history")
    suspend fun getAssetHistory(
        @Path("id") assetId: String,
        @Query("interval") interval: String = "h1",
        @Query("start") start: Long? = null,
        @Query("end") end: Long? = null
    ): Response<HistoryResponse>
}
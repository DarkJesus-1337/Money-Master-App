package com.pixelpioneer.moneymaster.data.services

import com.pixelpioneer.moneymaster.data.model.AssetsResponse
import com.pixelpioneer.moneymaster.data.model.HistoryResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Service interface for accessing CoinCap API endpoints.
 *
 * Provides methods to retrieve cryptocurrency assets and their historical price data.
 */
interface CoinCapApiService {

    /**
     * Retrieves a list of cryptocurrency assets.
     *
     * @param limit The maximum number of assets to return. Default is 10.
     * @return A [Response] containing [AssetsResponse] with asset data.
     */
    @GET("assets")
    suspend fun getAssets(
        @Query("limit") limit: Int = 10
    ): Response<AssetsResponse>

    /**
     * Retrieves historical price data for a specific asset.
     *
     * @param assetId The ID of the asset.
     * @param interval The interval for historical data (e.g., "h1" for hourly). Default is "h1".
     * @param start Optional start timestamp in milliseconds.
     * @param end Optional end timestamp in milliseconds.
     * @return A [Response] containing [HistoryResponse] with historical price data.
     */
    @GET("assets/{id}/history")
    suspend fun getAssetHistory(
        @Path("id") assetId: String,
        @Query("interval") interval: String = "h1",
        @Query("start") start: Long? = null,
        @Query("end") end: Long? = null
    ): Response<HistoryResponse>
}
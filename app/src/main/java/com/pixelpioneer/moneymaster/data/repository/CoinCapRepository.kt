package com.pixelpioneer.moneymaster.data.repository

import android.util.Log
import com.pixelpioneer.moneymaster.data.model.Asset
import com.pixelpioneer.moneymaster.data.model.HistoryDataPoint
import com.pixelpioneer.moneymaster.data.services.CoinCapApiService
import java.util.Calendar

/**
 * Repository for accessing cryptocurrency asset data from the CoinCap API.
 *
 * Provides methods to fetch asset lists and historical price data.
 *
 * @property api Service for CoinCap API requests.
 */
class CoinCapRepository(private val api: CoinCapApiService) {

    /**
     * Retrieves a list of cryptocurrency assets from the CoinCap API.
     *
     * @param limit The maximum number of assets to retrieve. Default is 10.
     * @return A list of [Asset] objects, or an empty list if the request fails.
     */
    suspend fun getAssets(limit: Int = 10): List<Asset> {
        Log.d("CoinCapRepository", "API call gestartet")
        return try {
            val response = api.getAssets(limit)
            Log.d("CoinCapRepository", "Antwort: $response")
            response.body()?.data ?: emptyList()
        } catch (e: Exception) {
            Log.e("CoinCapRepository", "Fehler beim Laden der Daten", e)
            emptyList()
        }
    }

    /**
     * Retrieves historical price data for a specific cryptocurrency asset.
     *
     * @param assetId The unique identifier of the asset.
     * @param interval The interval for historical data (e.g., "h1" for hourly). Default is "h1".
     * @param daysBack The number of days back from today to retrieve data for. Default is 7.
     * @return A list of [HistoryDataPoint] objects, or an empty list if the request fails.
     */
    suspend fun getAssetHistory(
        assetId: String,
        interval: String = "h1",
        daysBack: Int = 7
    ): List<HistoryDataPoint> {
        return try {
            val calendar = Calendar.getInstance()
            val endTime = calendar.timeInMillis

            calendar.add(Calendar.DAY_OF_YEAR, -daysBack)
            val startTime = calendar.timeInMillis

            val response = api.getAssetHistory(
                assetId = assetId,
                interval = interval,
                start = startTime,
                end = endTime
            )

            Log.d(
                "CoinCapRepository",
                "Historie für $assetId: ${response.body()?.data?.size} Datenpunkte"
            )
            response.body()?.data ?: emptyList()
        } catch (e: Exception) {
            Log.e("CoinCapRepository", "Fehler beim Laden der Historie für $assetId", e)
            emptyList()
        }
    }
}

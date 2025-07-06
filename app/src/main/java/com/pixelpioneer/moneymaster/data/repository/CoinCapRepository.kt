package com.pixelpioneer.moneymaster.data.repository

import android.util.Log
import com.pixelpioneer.moneymaster.data.model.Asset
import com.pixelpioneer.moneymaster.data.model.HistoryDataPoint
import com.pixelpioneer.moneymaster.data.services.CoinCapApiService
import java.util.Calendar

class CoinCapRepository(private val api: CoinCapApiService) {

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

            Log.d("CoinCapRepository", "Historie für $assetId: ${response.body()?.data?.size} Datenpunkte")
            response.body()?.data ?: emptyList()
        } catch (e: Exception) {
            Log.e("CoinCapRepository", "Fehler beim Laden der Historie für $assetId", e)
            emptyList()
        }
    }
}

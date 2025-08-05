package com.pixelpioneer.moneymaster.data.model

/**
 * Represents a single historical price data point for a cryptocurrency.
 *
 * @property priceUsd The price in USD at the specified time
 * @property time The timestamp in milliseconds
 * @property date The formatted date string
 */
data class HistoryDataPoint(
    val priceUsd: String,
    val time: Long,
    val date: String
)

/**
 * Response wrapper for historical price data.
 *
 * @property data The list of historical data points
 * @property timestamp The server timestamp when the data was generated
 */
data class HistoryResponse(
    val data: List<HistoryDataPoint>,
    val timestamp: Long
)
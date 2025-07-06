package com.pixelpioneer.moneymaster.data.model

data class HistoryDataPoint(
    val priceUsd: String,
    val time: Long,
    val date: String
)

data class HistoryResponse(
    val data: List<HistoryDataPoint>,
    val timestamp: Long
)
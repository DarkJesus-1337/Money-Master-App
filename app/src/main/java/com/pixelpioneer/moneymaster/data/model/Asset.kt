package com.pixelpioneer.moneymaster.data.model

data class Asset(
    val id: String,
    val rank: String,
    val symbol: String,
    val name: String,
    val supply: String,
    val maxSupply: String?,
    val marketCapUsd: String,
    val volumeUsd24Hr: String,
    val priceUsd: String,
    val changePercent24Hr: String,
    val vwap24Hr: String?,
    val explorer: String?
)

data class AssetsResponse(
    val data: List<Asset>,
    val timestamp: Long
)
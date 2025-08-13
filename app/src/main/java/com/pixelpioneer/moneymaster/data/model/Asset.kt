package com.pixelpioneer.moneymaster.data.model

/**
 * Represents a cryptocurrency asset with market data.
 *
 * This data class encapsulates all the information about a cryptocurrency asset
 * as provided by the CoinCap API.
 *
 * @property id Unique identifier for the asset
 * @property rank Market rank of the asset
 * @property symbol Trading symbol/ticker of the asset
 * @property name Full name of the cryptocurrency
 * @property supply Current circulating supply of the asset
 * @property maxSupply Maximum possible supply of the asset, null if unlimited
 * @property marketCapUsd Total market capitalization in USD
 * @property volumeUsd24Hr Trading volume in the last 24 hours in USD
 * @property priceUsd Current price in USD
 * @property changePercent24Hr Price change percentage in the last 24 hours
 * @property vwap24Hr Volume weighted average price in the last 24 hours
 * @property explorer URL to the asset's blockchain explorer
 */
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

/**
 * Response wrapper for a list of cryptocurrency assets.
 *
 * @property data The list of cryptocurrency assets
 * @property timestamp The server timestamp when the data was generated
 */
data class AssetsResponse(
    val data: List<Asset>,
    val timestamp: Long
)
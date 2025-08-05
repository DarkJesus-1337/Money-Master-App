package com.pixelpioneer.moneymaster.data.model

/**
 * Data class representing financial trends for a single month.
 *
 * @property monthYear The month and year (e.g., "January 2024").
 * @property income Total income for the month.
 * @property expenses Total expenses for the month.
 * @property balance Net balance for the month.
 * @property transactionCount Number of transactions in the month.
 */
data class MonthlyTrend(
    val monthYear: String,
    val income: Double,
    val expenses: Double,
    val balance: Double,
    val transactionCount: Int
)
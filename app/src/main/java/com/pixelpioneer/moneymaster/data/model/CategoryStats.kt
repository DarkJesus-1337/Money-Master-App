package com.pixelpioneer.moneymaster.data.model

/**
 * Data class representing statistics for a single transaction category.
 *
 * @property category The transaction category.
 * @property amount The total amount spent in this category.
 * @property transactionCount The number of transactions in this category.
 */
data class CategoryStats(
    val category: TransactionCategory,
    val amount: Double,
    val transactionCount: Int
)

package com.pixelpioneer.moneymaster.data.model

/**
 * Represents a financial transaction in the application.
 *
 * This class encapsulates all information about a single financial transaction,
 * including amount, category, date, and whether it's an expense or income.
 *
 * @property id Unique identifier for the transaction
 * @property amount The monetary amount of the transaction
 * @property title Short title or name of the transaction
 * @property description Optional detailed description of the transaction
 * @property category The category this transaction belongs to
 * @property date Timestamp when this transaction occurred (in milliseconds)
 * @property isExpense Whether this transaction is an expense (true) or income (false)
 */
data class Transaction(
    val id: Long = 0,
    val amount: Double,
    val title: String,
    val description: String = "",
    val category: TransactionCategory,
    val date: Long,
    val isExpense: Boolean = true
)
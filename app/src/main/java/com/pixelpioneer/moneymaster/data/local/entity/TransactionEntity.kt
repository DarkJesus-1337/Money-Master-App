package com.pixelpioneer.moneymaster.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity representing a financial transaction in the database.
 *
 * A transaction records a financial activity, which can be either an expense
 * or income, with an amount, description, and category.
 *
 * @property id The unique identifier for the transaction
 * @property amount The monetary amount of the transaction
 * @property title The short title or name of the transaction
 * @property description Additional details about the transaction (optional)
 * @property categoryId The ID of the category this transaction belongs to
 * @property date The timestamp when this transaction occurred (in milliseconds)
 * @property isExpense Whether this transaction is an expense (true) or income (false)
 */
@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val amount: Double,
    val title: String,
    val description: String = "",
    val categoryId: Long,
    val date: Long,
    val isExpense: Boolean = true
)

package com.pixelpioneer.moneymaster.data.model

import com.pixelpioneer.moneymaster.data.enums.BudgetPeriod

/**
 * Represents a spending budget for a specific transaction category.
 *
 * This class defines a budget with an amount limit for a specific category
 * over a defined time period (e.g., weekly, monthly).
 *
 * @property id Unique identifier for the budget
 * @property category The transaction category this budget applies to
 * @property amount The maximum amount allocated for this budget
 * @property period The time period for which this budget applies
 * @property spent The current amount spent against this budget
 */
data class Budget(
    val id: Long = 0,
    val category: TransactionCategory,
    val amount: Double,
    val period: BudgetPeriod,
    val spent: Double = 0.0
)
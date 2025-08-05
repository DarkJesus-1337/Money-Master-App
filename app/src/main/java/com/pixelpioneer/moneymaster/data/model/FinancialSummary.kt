package com.pixelpioneer.moneymaster.data.model

/**
 * Data class representing a summary of financial data.
 *
 * @property totalIncome The total income.
 * @property totalExpenses The total expenses.
 * @property balance The net balance (income minus expenses).
 */
data class FinancialSummary(
    val totalIncome: Double = 0.0,
    val totalExpenses: Double = 0.0,
    val balance: Double = 0.0
)
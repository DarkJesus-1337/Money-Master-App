package com.pixelpioneer.moneymaster.data.model

/**
 * Data class representing an overview of statistics for the current month.
 *
 * @property monthlyIncome Total income for the month.
 * @property monthlyExpenses Total expenses for the month.
 * @property monthlyBalance Net balance for the month.
 * @property avgDailyExpenses Average daily expenses for the month.
 * @property avgWeeklyExpenses Average weekly expenses for the month.
 * @property totalTransactions Total number of transactions overall.
 * @property monthlyTransactions Number of transactions in the current month.
 */
data class StatisticsOverview(
    val monthlyIncome: Double,
    val monthlyExpenses: Double,
    val monthlyBalance: Double,
    val avgDailyExpenses: Double,
    val avgWeeklyExpenses: Double,
    val totalTransactions: Int,
    val monthlyTransactions: Int
)
package com.pixelpioneer.moneymaster.data.model

import com.pixelpioneer.moneymaster.data.enums.BudgetPeriod

data class Budget(
    val id: Long = 0,
    val category: TransactionCategory,
    val amount: Double,
    val period: BudgetPeriod,
    val spent: Double = 0.0
)
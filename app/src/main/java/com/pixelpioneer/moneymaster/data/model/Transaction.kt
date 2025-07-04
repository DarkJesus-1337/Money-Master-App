package com.pixelpioneer.moneymaster.data.model

data class Transaction(
    val id: Long = 0,
    val amount: Double,
    val title: String,
    val description: String = "",
    val category: TransactionCategory,
    val date: Long,
    val isExpense: Boolean = true
)
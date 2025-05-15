package com.pixelpioneer.moneymaster.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

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
// Beziehungen mit Room

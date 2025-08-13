package com.pixelpioneer.moneymaster.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Entity representing a budget in the database.
 *
 * A budget is linked to a specific category and specifies a spending limit
 * for that category over a certain period.
 *
 * @property id The unique identifier for the budget
 * @property categoryId The ID of the category this budget is for
 * @property amount The monetary amount of the budget limit
 * @property periodName The time period for the budget (e.g., "WEEKLY", "MONTHLY")
 */
@Entity(
    tableName = "budgets",
    foreignKeys = [
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["categoryId"])]
)
data class BudgetEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val categoryId: Long,
    val amount: Double,
    @ColumnInfo(name = "period") val periodName: String
)
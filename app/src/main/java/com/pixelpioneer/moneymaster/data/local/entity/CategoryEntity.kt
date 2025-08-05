package com.pixelpioneer.moneymaster.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity representing a transaction category in the database.
 *
 * Categories are used to organize transactions and budgets by type
 * (e.g., Food, Transportation, Entertainment).
 *
 * @property id The unique identifier for the category
 * @property name The display name of the category
 * @property color The color associated with this category (as an integer color value)
 * @property iconResId The resource ID for the icon representing this category
 */
@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val color: Int,
    val iconResId: Int
)
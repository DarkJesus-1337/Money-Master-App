package com.pixelpioneer.moneymaster.data.model

/**
 * Represents a category for classifying transactions.
 *
 * Categories help organize transactions by type and provide visual identification
 * through colors and icons.
 *
 * @property id Unique identifier for the category
 * @property name Display name of the category
 * @property color Color associated with this category (as an integer color value)
 * @property icon Resource ID for the icon representing this category
 */
data class TransactionCategory(
    val id: Long = 0,
    val name: String,
    val color: Int,
    val icon: Int
)

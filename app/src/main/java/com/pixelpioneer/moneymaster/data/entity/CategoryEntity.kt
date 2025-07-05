package com.pixelpioneer.moneymaster.data.entity

import androidx.room.*

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val color: Int,
    val iconResId: Int
)
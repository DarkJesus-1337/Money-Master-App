package com.pixelpioneer.moneymaster.data.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.pixelpioneer.moneymaster.data.entity.CategoryEntity
import com.pixelpioneer.moneymaster.data.entity.TransactionEntity

data class TransactionWithCategory(
    @Embedded val transaction: TransactionEntity,
    @Relation(
        parentColumn = "categoryId",
        entityColumn = "id"
    )
    val category: CategoryEntity
)
package com.pixelpioneer.moneymaster.data.local.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.pixelpioneer.moneymaster.data.local.entity.CategoryEntity
import com.pixelpioneer.moneymaster.data.local.entity.TransactionEntity

data class TransactionWithCategory(
    @Embedded val transaction: TransactionEntity,
    @Relation(
        parentColumn = "categoryId",
        entityColumn = "id"
    )
    val category: CategoryEntity
)
package com.pixelpioneer.moneymaster.data.local.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.pixelpioneer.moneymaster.data.local.entity.CategoryEntity
import com.pixelpioneer.moneymaster.data.local.entity.TransactionEntity

/**
 * Represents a relationship between a Transaction and its Category.
 *
 * This class is used by Room to fetch a transaction together with its related category
 * in a single query, providing a more complete view of transaction data.
 *
 * @property transaction The transaction entity
 * @property category The category entity that this transaction is associated with
 */
data class TransactionWithCategory(
    @Embedded val transaction: TransactionEntity,
    @Relation(
        parentColumn = "categoryId",
        entityColumn = "id"
    )
    val category: CategoryEntity
)
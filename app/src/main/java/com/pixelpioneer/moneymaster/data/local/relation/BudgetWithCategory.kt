package com.pixelpioneer.moneymaster.data.local.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.pixelpioneer.moneymaster.data.local.entity.BudgetEntity
import com.pixelpioneer.moneymaster.data.local.entity.CategoryEntity

/**
 * Represents a relationship between a Budget and its Category.
 *
 * This class is used by Room to fetch a budget together with its related category
 * in a single query, providing a more complete view of budget data.
 *
 * @property budget The budget entity
 * @property category The category entity that this budget is associated with
 */
data class BudgetWithCategory(
    @Embedded val budget: BudgetEntity,
    @Relation(
        parentColumn = "categoryId",
        entityColumn = "id"
    )
    val category: CategoryEntity
)
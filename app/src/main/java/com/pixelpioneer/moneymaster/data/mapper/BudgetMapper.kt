package com.pixelpioneer.moneymaster.data.mapper

import com.pixelpioneer.moneymaster.data.enums.BudgetPeriod
import com.pixelpioneer.moneymaster.data.local.entity.BudgetEntity
import com.pixelpioneer.moneymaster.data.local.relation.BudgetWithCategory
import com.pixelpioneer.moneymaster.data.model.Budget
import com.pixelpioneer.moneymaster.data.model.TransactionCategory

/**
 * Mapper class for converting between Budget domain models and database entities.
 *
 * This object provides utility methods to transform Budget objects to BudgetEntity
 * objects and vice versa.
 */
object BudgetMapper {
    /**
     * Converts a Budget domain model to a BudgetEntity database entity.
     *
     * @param budget The Budget domain model to convert
     * @return A BudgetEntity database entity
     */
    fun toEntity(budget: Budget): BudgetEntity {
        return BudgetEntity(
            id = budget.id,
            categoryId = budget.category.id,
            amount = budget.amount,
            periodName = budget.period.name
        )
    }

    /**
     * Converts a BudgetWithCategory database relation to a Budget domain model.
     *
     * @param entity The BudgetWithCategory database relation to convert
     * @param spent The current amount spent against this budget (default: 0.0)
     * @return A Budget domain model
     */
    fun fromEntity(entity: BudgetWithCategory, spent: Double = 0.0): Budget {
        return Budget(
            id = entity.budget.id,
            category = TransactionCategory(
                id = entity.category.id,
                name = entity.category.name,
                color = entity.category.color,
                icon = entity.category.iconResId
            ),
            amount = entity.budget.amount,
            period = BudgetPeriod.valueOf(entity.budget.periodName),
            spent = spent
        )
    }
}
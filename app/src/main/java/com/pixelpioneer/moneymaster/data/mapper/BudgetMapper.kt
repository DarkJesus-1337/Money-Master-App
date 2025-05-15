package com.pixelpioneer.moneymaster.data.mapper

import com.pixelpioneer.moneymaster.data.entity.BudgetEntity
import com.pixelpioneer.moneymaster.data.enums.BudgetPeriod
import com.pixelpioneer.moneymaster.data.model.TransactionCategory
import com.pixelpioneer.moneymaster.data.relation.BudgetWithCategory

object BudgetMapper {
    fun toEntity(budget: Budget): BudgetEntity {
        return BudgetEntity(
            id = budget.id,
            categoryId = budget.category.id,
            amount = budget.amount,
            periodName = budget.period.name
        )
    }

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
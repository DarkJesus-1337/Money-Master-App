package com.pixelpioneer.moneymaster.data.repository

import com.pixelpioneer.moneymaster.data.db.BudgetDao
import com.pixelpioneer.moneymaster.data.db.TransactionDao
import com.pixelpioneer.moneymaster.data.entity.BudgetEntity
import com.pixelpioneer.moneymaster.data.enums.BudgetPeriod
import com.pixelpioneer.moneymaster.data.model.Budget
import com.pixelpioneer.moneymaster.data.model.TransactionCategory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Calendar

class BudgetRepository(
    private val budgetDao: BudgetDao,
    private val transactionDao: TransactionDao
) {
    val allBudgetsWithSpending = budgetDao.getBudgetsWithCategory().map { budgets ->
        budgets.map { budgetWithCategory ->
            val period = BudgetPeriod.valueOf(budgetWithCategory.budget.periodName)
            val (startDate, endDate) = getDateRangeForBudgetPeriod(period)

            Budget(
                id = budgetWithCategory.budget.id,
                category = TransactionCategory(
                    id = budgetWithCategory.category.id,
                    name = budgetWithCategory.category.name,
                    color = budgetWithCategory.category.color,
                    icon = budgetWithCategory.category.iconResId
                ),
                amount = budgetWithCategory.budget.amount,
                period = period,
                spent = 0.0 // Wird später asynchron berechnet
            )
        }
    }

    fun getBudgetById(id: Long): Flow<Budget> {
        return budgetDao.getBudgetWithCategoryById(id).map { budgetWithCategory ->
            Budget(
                id = budgetWithCategory.budget.id,
                category = TransactionCategory(
                    id = budgetWithCategory.category.id,
                    name = budgetWithCategory.category.name,
                    color = budgetWithCategory.category.color,
                    icon = budgetWithCategory.category.iconResId
                ),
                amount = budgetWithCategory.budget.amount,
                period = BudgetPeriod.valueOf(budgetWithCategory.budget.periodName),
                spent = 0.0
            )
        }
    }

    suspend fun insertBudget(budget: Budget): Long {
        val entity = BudgetEntity(
            id = budget.id,
            categoryId = budget.category.id,
            amount = budget.amount,
            periodName = budget.period.name
        )
        return budgetDao.insertBudget(entity)
    }

    suspend fun updateBudget(budget: Budget) {
        val entity = BudgetEntity(
            id = budget.id,
            categoryId = budget.category.id,
            amount = budget.amount,
            periodName = budget.period.name
        )
        budgetDao.updateBudget(entity)
    }

    suspend fun deleteBudget(budget: Budget) {
        val entity = BudgetEntity(
            id = budget.id,
            categoryId = budget.category.id,
            amount = budget.amount,
            periodName = budget.period.name
        )
        budgetDao.deleteBudget(entity)
    }

    fun getSpentAmountForBudget(budget: Budget): Flow<Double> {
        val (startDate, endDate) = getDateRangeForBudgetPeriod(budget.period)
        
        return transactionDao.getTotalExpensesByCategoryAndDateRange(
            budget.category.id, 
            startDate, 
            endDate
        ).map { it ?: 0.0 }
    }

    suspend fun getBudgetsWithSpendingSync(): List<Budget> {
        val budgetsWithCategory = budgetDao.getBudgetsWithCategorySync()

        return budgetsWithCategory.map { budgetWithCategory ->
            val period = BudgetPeriod.valueOf(budgetWithCategory.budget.periodName)
            val (startDate, endDate) = getDateRangeForBudgetPeriod(period)

            val spentAmount = calculateSpentAmount(budgetWithCategory.category.id, startDate, endDate)

            Budget(
                id = budgetWithCategory.budget.id,
                category = TransactionCategory(
                    id = budgetWithCategory.category.id,
                    name = budgetWithCategory.category.name,
                    color = budgetWithCategory.category.color,
                    icon = budgetWithCategory.category.iconResId
                ),
                amount = budgetWithCategory.budget.amount,
                period = period,
                spent = spentAmount
            )
        }
    }

    private fun getDateRangeForBudgetPeriod(period: BudgetPeriod): Pair<Long, Long> {
        val calendar = Calendar.getInstance()
        val endDate = calendar.timeInMillis
        
        when (period) {
            BudgetPeriod.DAILY -> {
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
            }
            BudgetPeriod.WEEKLY -> {
                calendar.set(Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek)
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
            }
            BudgetPeriod.MONTHLY -> {
                calendar.set(Calendar.DAY_OF_MONTH, 1)
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
            }
            BudgetPeriod.YEARLY -> {
                calendar.set(Calendar.DAY_OF_YEAR, 1)
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
            }
        }
        
        val startDate = calendar.timeInMillis
        return Pair(startDate, endDate)
    }

    private suspend fun calculateSpentAmount(categoryId: Long, startDate: Long, endDate: Long): Double {
        return try {
            // Verwende eine suspendierte Funktion, um die Ausgaben zu berechnen
            transactionDao.getTotalExpensesByCategoryAndDateRangeSync(categoryId, startDate, endDate) ?: 0.0
        } catch (e: Exception) {
            0.0
        }
    }
}
package com.pixelpioneer.moneymaster.data.repository

import com.pixelpioneer.moneymaster.data.db.BudgetDao
import com.pixelpioneer.moneymaster.data.db.TransactionDao
import com.pixelpioneer.moneymaster.data.entity.BudgetEntity
import com.pixelpioneer.moneymaster.data.enums.BudgetPeriod
import com.pixelpioneer.moneymaster.data.model.Budget
import com.pixelpioneer.moneymaster.data.model.TransactionCategory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import java.util.Calendar

class BudgetRepository(
    private val budgetDao: BudgetDao,
    private val transactionDao: TransactionDao
) {
    // Get all budgets with spending information
    val allBudgetsWithSpending = budgetDao.getBudgetsWithCategory().map { budgets ->
        budgets.map { budgetWithCategory ->
            // Create a Budget object from the database entity
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
                spent = 0.0 // We'll update this in a separate step
            )
        }
    }

    // Get budget by ID with spending information
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
                spent = 0.0 // We'll calculate this in the ViewModel
            )
        }
    }

    // Insert a new budget
    suspend fun insertBudget(budget: Budget): Long {
        val entity = BudgetEntity(
            id = budget.id,
            categoryId = budget.category.id,
            amount = budget.amount,
            periodName = budget.period.name
        )
        return budgetDao.insertBudget(entity)
    }

    // Update an existing budget
    suspend fun updateBudget(budget: Budget) {
        val entity = BudgetEntity(
            id = budget.id,
            categoryId = budget.category.id,
            amount = budget.amount,
            periodName = budget.period.name
        )
        budgetDao.updateBudget(entity)
    }

    // Delete a budget
    suspend fun deleteBudget(budget: Budget) {
        val entity = BudgetEntity(
            id = budget.id,
            categoryId = budget.category.id,
            amount = budget.amount,
            periodName = budget.period.name
        )
        budgetDao.deleteBudget(entity)
    }

    // Calculate spent amount for a budget based on its period
    fun getSpentAmountForBudget(budget: Budget): Flow<Double> {
        // Calculate date range based on budget period
        val (startDate, endDate) = getDateRangeForBudgetPeriod(budget.period)
        
        // Get total expenses for the category within the date range
        return transactionDao.getTotalExpensesByCategoryAndDateRange(
            budget.category.id, 
            startDate, 
            endDate
        ).map { it ?: 0.0 } // Convert null to 0.0
    }

    // Get all budgets with spent amounts
    fun getBudgetsWithSpending(): Flow<List<Budget>> {
        val budgetsFlow = budgetDao.getBudgetsWithCategory()
        
        return budgetsFlow.map { budgetList ->
            budgetList.map { budgetWithCategory ->
                // For each budget, calculate the date range based on its period
                val period = BudgetPeriod.valueOf(budgetWithCategory.budget.periodName)
                val (startDate, endDate) = getDateRangeForBudgetPeriod(period)
                
                // Create a Budget object with current spending
                val spentAmount = transactionDao.getTotalExpensesByCategoryAndDateRange(
                    budgetWithCategory.category.id,
                    startDate,
                    endDate
                ).map { it ?: 0.0 }
                
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
                    spent = 0.0 // This will be updated in the ViewModel
                )
            }
        }
    }

    // Helper method to calculate date range based on budget period
    private fun getDateRangeForBudgetPeriod(period: BudgetPeriod): Pair<Long, Long> {
        val calendar = Calendar.getInstance()
        val endDate = calendar.timeInMillis
        
        when (period) {
            BudgetPeriod.DAILY -> {
                // Start of the current day
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
            }
            BudgetPeriod.WEEKLY -> {
                // Start of the current week (assuming first day is Sunday)
                calendar.set(Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek)
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
            }
            BudgetPeriod.MONTHLY -> {
                // Start of the current month
                calendar.set(Calendar.DAY_OF_MONTH, 1)
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
            }
            BudgetPeriod.YEARLY -> {
                // Start of the current year
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
}
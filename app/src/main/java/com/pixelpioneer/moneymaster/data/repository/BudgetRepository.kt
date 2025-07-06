package com.pixelpioneer.moneymaster.data.repository

import com.pixelpioneer.moneymaster.data.db.BudgetDao
import com.pixelpioneer.moneymaster.data.db.TransactionDao
import com.pixelpioneer.moneymaster.data.entity.BudgetEntity
import com.pixelpioneer.moneymaster.data.enums.BudgetPeriod
import com.pixelpioneer.moneymaster.data.model.Budget
import com.pixelpioneer.moneymaster.data.model.TransactionCategory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.util.Calendar

class BudgetRepository(
    private val budgetDao: BudgetDao,
    private val transactionDao: TransactionDao
) {
    val allBudgetsWithSpending: Flow<List<Budget>> =
        combine(
            budgetDao.getBudgetsWithCategory(),
            transactionDao.getTransactionsWithCategory()
        ) { budgets, transactions ->
            budgets.map { budgetWithCategory ->
                val period = BudgetPeriod.valueOf(budgetWithCategory.budget.periodName)
                val (startDate, endDate) = getDateRangeForBudgetPeriod(period)

                // Berechne ausgaben basierend auf den Transaktionen
                val spentAmount = transactions
                    .filter { transaction ->
                        transaction.category.id == budgetWithCategory.category.id &&
                                transaction.transaction.isExpense &&
                                transaction.transaction.date >= startDate &&
                                transaction.transaction.date <= endDate
                    }
                    .sumOf { it.transaction.amount }

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

    fun getBudgetById(id: Long): Flow<Budget> =
        combine(
            budgetDao.getBudgetWithCategoryById(id),
            transactionDao.getTransactionsWithCategory()
        ) { budgetWithCategory, transactions ->
            val period = BudgetPeriod.valueOf(budgetWithCategory.budget.periodName)
            val (startDate, endDate) = getDateRangeForBudgetPeriod(period)

            // Berechne ausgaben basierend auf den Transaktionen
            val spentAmount = transactions
                .filter { transaction ->
                    transaction.category.id == budgetWithCategory.category.id &&
                            transaction.transaction.isExpense &&
                            transaction.transaction.date >= startDate &&
                            transaction.transaction.date <= endDate
                }
                .sumOf { it.transaction.amount }

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

    suspend fun getBudgetsWithSpendingSync(): List<Budget> {
        val budgetsWithCategory = budgetDao.getBudgetsWithCategorySync()
        return budgetsWithCategory.map { budgetWithCategory ->
            val period = BudgetPeriod.valueOf(budgetWithCategory.budget.periodName)
            val (startDate, endDate) = getDateRangeForBudgetPeriod(period)
            val spentAmount = transactionDao.getTotalExpensesByCategoryAndDateRangeSync(
                budgetWithCategory.category.id,
                startDate,
                endDate
            ) ?: 0.0

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
        val currentTime = calendar.timeInMillis

        when (period) {
            BudgetPeriod.DAILY -> {
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
                val startDate = calendar.timeInMillis

                calendar.set(Calendar.HOUR_OF_DAY, 23)
                calendar.set(Calendar.MINUTE, 59)
                calendar.set(Calendar.SECOND, 59)
                calendar.set(Calendar.MILLISECOND, 999)
                val endDate = calendar.timeInMillis

                return Pair(startDate, endDate)
            }

            BudgetPeriod.WEEKLY -> {
                calendar.set(Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek)
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
                val startDate = calendar.timeInMillis

                return Pair(startDate, currentTime)
            }

            BudgetPeriod.MONTHLY -> {
                calendar.set(Calendar.DAY_OF_MONTH, 1)
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
                val startDate = calendar.timeInMillis

                return Pair(startDate, currentTime)
            }

            BudgetPeriod.YEARLY -> {
                calendar.set(Calendar.DAY_OF_YEAR, 1)
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
                val startDate = calendar.timeInMillis

                return Pair(startDate, currentTime)
            }
        }
    }
}
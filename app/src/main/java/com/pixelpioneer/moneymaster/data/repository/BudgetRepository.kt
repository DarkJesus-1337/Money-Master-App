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

/**
 * Repository for managing budget data and related operations.
 *
 * Provides methods to retrieve budgets with spending information,
 * insert, update, and delete budgets, and calculate spending for different budget periods.
 *
 * @property budgetDao Data access object for budget entities.
 * @property transactionDao Data access object for transaction entities.
 */
class BudgetRepository(
    private val budgetDao: BudgetDao,
    private val transactionDao: TransactionDao
) {
    /**
     * Flow of all budgets with their current spending amounts.
     *
     * Combines budget data with transaction data to calculate spending
     * for each budget based on its period and category.
     */
    val allBudgetsWithSpending: Flow<List<Budget>> =
        combine(
            budgetDao.getBudgetsWithCategory(),
            transactionDao.getTransactionsWithCategory()
        ) { budgets, transactions ->
            budgets.map { budgetWithCategory ->
                val period = BudgetPeriod.valueOf(budgetWithCategory.budget.periodName)
                val (startDate, endDate) = getDateRangeForBudgetPeriod(period)

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

    /**
     * Retrieves a specific budget by ID with its current spending.
     *
     * @param id The unique identifier of the budget.
     * @return A [Flow] containing the [Budget] with spending information.
     */
    fun getBudgetById(id: Long): Flow<Budget> =
        combine(
            budgetDao.getBudgetWithCategoryById(id),
            transactionDao.getTransactionsWithCategory()
        ) { budgetWithCategory, transactions ->
            val period = BudgetPeriod.valueOf(budgetWithCategory.budget.periodName)
            val (startDate, endDate) = getDateRangeForBudgetPeriod(period)

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

    /**
     * Inserts a new budget into the database.
     *
     * @param budget The budget to insert.
     * @return The ID of the newly inserted budget.
     */
    suspend fun insertBudget(budget: Budget): Long {
        val entity = BudgetEntity(
            id = budget.id,
            categoryId = budget.category.id,
            amount = budget.amount,
            periodName = budget.period.name
        )
        return budgetDao.insertBudget(entity)
    }

    /**
     * Updates an existing budget in the database.
     *
     * @param budget The budget with updated information.
     */
    suspend fun updateBudget(budget: Budget) {
        val entity = BudgetEntity(
            id = budget.id,
            categoryId = budget.category.id,
            amount = budget.amount,
            periodName = budget.period.name
        )
        budgetDao.updateBudget(entity)
    }

    /**
     * Deletes a budget from the database.
     *
     * @param budget The budget to delete.
     */
    suspend fun deleteBudget(budget: Budget) {
        val entity = BudgetEntity(
            id = budget.id,
            categoryId = budget.category.id,
            amount = budget.amount,
            periodName = budget.period.name
        )
        budgetDao.deleteBudget(entity)
    }

    /**
     * Retrieves all budgets with spending information synchronously.
     *
     * This method performs synchronous database operations and should
     * only be used when necessary (e.g., in background tasks).
     *
     * @return A list of [Budget] objects with spending information.
     */
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

    /**
     * Calculates the date range for a given budget period.
     *
     * @param period The budget period to calculate the range for.
     * @return A [Pair] containing start and end timestamps in milliseconds.
     */
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
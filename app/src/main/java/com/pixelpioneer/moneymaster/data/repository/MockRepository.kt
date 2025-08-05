package com.pixelpioneer.moneymaster.data.repository

import com.pixelpioneer.moneymaster.data.enums.BudgetPeriod
import com.pixelpioneer.moneymaster.data.model.AssetsResponse
import com.pixelpioneer.moneymaster.data.model.Budget
import com.pixelpioneer.moneymaster.data.model.HistoryResponse
import com.pixelpioneer.moneymaster.data.model.Receipt
import com.pixelpioneer.moneymaster.data.model.Transaction
import com.pixelpioneer.moneymaster.data.model.TransactionCategory
import com.pixelpioneer.moneymaster.preview.SampleData
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.random.Random

/**
 * Mock repository for testing and UI preview purposes.
 *
 * Provides in-memory implementations of all repository functionality with
 * artificial delays to simulate network and database operations.
 */
class MockRepository {

    private val _transactions = SampleData.sampleTransactions.toMutableList()

    /**
     * Returns a flow of all transactions.
     *
     * @return Flow emitting a list of all transactions with artificial delay
     */
    fun getAllTransactions(): Flow<List<Transaction>> = flow {
        delay(500)
        emit(_transactions.toList())
    }

    /**
     * Inserts a new transaction.
     *
     * @param transaction The transaction to insert
     * @return Generated ID for the new transaction
     */
    suspend fun insertTransaction(transaction: Transaction): Long {
        delay(200)
        val newId = (_transactions.maxOfOrNull { it.id } ?: 0) + 1
        val newTransaction = transaction.copy(id = newId)
        _transactions.add(newTransaction)
        return newId
    }

    /**
     * Updates an existing transaction.
     *
     * @param transaction The transaction with updated values
     */
    suspend fun updateTransaction(transaction: Transaction) {
        delay(200)
        val index = _transactions.indexOfFirst { it.id == transaction.id }
        if (index != -1) {
            _transactions[index] = transaction
        }
    }

    /**
     * Deletes a transaction.
     *
     * @param transaction The transaction to delete
     */
    suspend fun deleteTransaction(transaction: Transaction) {
        delay(200)
        _transactions.removeIf { it.id == transaction.id }
    }

    /**
     * Returns transactions filtered by category ID.
     *
     * @param categoryId The ID of the category to filter by
     * @return Flow emitting transactions for the specified category
     */
    fun getTransactionsByCategory(categoryId: Long): Flow<List<Transaction>> = flow {
        delay(300)
        emit(_transactions.filter { it.category.id == categoryId })
    }

    private val _categories = SampleData.sampleCategories.toMutableList()

    /**
     * Returns a flow of all categories.
     *
     * @return Flow emitting a list of all categories with artificial delay
     */
    fun getAllCategories(): Flow<List<TransactionCategory>> = flow {
        delay(300)
        emit(_categories.toList())
    }

    /**
     * Inserts a new category.
     *
     * @param category The category to insert
     * @return Generated ID for the new category
     */
    suspend fun insertCategory(category: TransactionCategory): Long {
        delay(200)
        val newId = (_categories.maxOfOrNull { it.id } ?: 0) + 1
        val newCategory = category.copy(id = newId)
        _categories.add(newCategory)
        return newId
    }

    /**
     * Updates an existing category.
     *
     * @param category The category with updated values
     */
    suspend fun updateCategory(category: TransactionCategory) {
        delay(200)
        val index = _categories.indexOfFirst { it.id == category.id }
        if (index != -1) {
            _categories[index] = category
        }
    }

    /**
     * Deletes a category.
     *
     * @param category The category to delete
     */
    suspend fun deleteCategory(category: TransactionCategory) {
        delay(200)
        _categories.removeIf { it.id == category.id }
    }

    // Mock Budget Repository
    private val _budgets = SampleData.sampleBudgets.toMutableList()

    /**
     * Returns a flow of all budgets.
     *
     * @return Flow emitting a list of all budgets with artificial delay
     */
    fun getAllBudgets(): Flow<List<Budget>> = flow {
        delay(400)
        emit(_budgets.toList())
    }

    /**
     * Inserts a new budget.
     *
     * @param budget The budget to insert
     * @return Generated ID for the new budget
     */
    suspend fun insertBudget(budget: Budget): Long {
        delay(200)
        val newId = (_budgets.maxOfOrNull { it.id } ?: 0) + 1
        val newBudget = budget.copy(id = newId)
        _budgets.add(newBudget)
        return newId
    }

    /**
     * Updates an existing budget.
     *
     * @param budget The budget with updated values
     */
    suspend fun updateBudget(budget: Budget) {
        delay(200)
        val index = _budgets.indexOfFirst { it.id == budget.id }
        if (index != -1) {
            _budgets[index] = budget
        }
    }

    /**
     * Deletes a budget.
     *
     * @param budget The budget to delete
     */
    suspend fun deleteBudget(budget: Budget) {
        delay(200)
        _budgets.removeIf { it.id == budget.id }
    }

    /**
     * Returns budgets filtered by period.
     *
     * @param period The budget period to filter by
     * @return Flow emitting budgets for the specified period
     */
    fun getBudgetsByPeriod(period: BudgetPeriod): Flow<List<Budget>> = flow {
        delay(300)
        emit(_budgets.filter { it.period == period })
    }

    /**
     * Simulates fetching cryptocurrency assets.
     *
     * @return Result containing either sample assets or a simulated error
     */
    suspend fun getAssets(): Result<AssetsResponse> {
        delay(1000)
        return if (Random.nextBoolean()) {
            Result.success(SampleData.sampleAssetsResponse)
        } else {
            Result.failure(Exception("Network error"))
        }
    }

    /**
     * Simulates fetching historical data for a cryptocurrency asset.
     *
     * @param assetId The ID of the asset to fetch history for
     * @return Result containing either sample history data or a simulated error
     */
    suspend fun getAssetHistory(assetId: String): Result<HistoryResponse> {
        delay(800)
        return if (Random.nextBoolean()) {
            Result.success(SampleData.sampleHistoryResponse)
        } else {
            Result.failure(Exception("Historical data not available"))
        }
    }

    /**
     * Simulates scanning a receipt from image data.
     *
     * @param imageData The image data of the receipt to scan
     * @return Result containing either a sample receipt or a simulated error
     */
    suspend fun scanReceipt(imageData: ByteArray): Result<Receipt> {
        delay(2000)
        return if (Random.nextBoolean()) {
            Result.success(SampleData.sampleReceipt)
        } else {
            Result.failure(Exception("Receipt could not be scanned"))
        }
    }

    /**
     * Returns transactions filtered by date range.
     *
     * @param startDate The start timestamp of the date range
     * @param endDate The end timestamp of the date range
     * @return Flow emitting transactions within the specified date range
     */
    fun getTransactionsByDateRange(startDate: Long, endDate: Long): Flow<List<Transaction>> = flow {
        delay(400)
        emit(_transactions.filter { it.date in startDate..endDate })
    }

    /**
     * Returns expenses grouped by category.
     *
     * @return Flow emitting a map of categories to their total expense amounts
     */
    fun getExpensesByCategory(): Flow<Map<TransactionCategory, Double>> = flow {
        delay(500)
        val expenses = _transactions.filter { it.isExpense }
            .groupBy { it.category }
            .mapValues { (_, transactions) -> transactions.sumOf { it.amount } }
        emit(expenses)
    }

    /**
     * Returns monthly totals for expenses.
     *
     * @return Flow emitting a map of month names to their total expense amounts
     */
    fun getMonthlyTotals(): Flow<Map<String, Double>> = flow {
        delay(600)
        val monthlyData = mapOf(
            "January" to 1250.50,
            "February" to 1180.25,
            "March" to 1320.75,
            "April" to 1095.30
        )
        emit(monthlyData)
    }
}
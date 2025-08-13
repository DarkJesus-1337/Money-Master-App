package com.pixelpioneer.moneymaster.data.local.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.pixelpioneer.moneymaster.data.local.entity.TransactionEntity
import com.pixelpioneer.moneymaster.data.local.relation.TransactionWithCategory
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for the transactions table.
 * Provides methods to query, insert, update and delete transaction data.
 */
@Dao
interface TransactionDao {
    /**
     * Gets all transactions from the database, ordered by date descending.
     *
     * @return Flow emitting a list of all transaction entities
     */
    @Query("SELECT * FROM transactions ORDER BY date DESC")
    fun getAllTransactions(): Flow<List<TransactionEntity>>

    /**
     * Gets a specific transaction by its ID.
     *
     * @param id The ID of the transaction to retrieve
     * @return Flow emitting the transaction entity
     */
    @Query("SELECT * FROM transactions WHERE id = :id")
    fun getTransactionById(id: Long): Flow<TransactionEntity>

    /**
     * Gets all transactions with their associated categories, ordered by date descending.
     *
     * @return Flow emitting a list of TransactionWithCategory objects
     */
    @Transaction
    @Query("SELECT * FROM transactions ORDER BY date DESC")
    fun getTransactionsWithCategory(): Flow<List<TransactionWithCategory>>

    /**
     * Gets a specific transaction with its associated category by transaction ID.
     *
     * @param id The ID of the transaction to retrieve
     * @return Flow emitting a TransactionWithCategory object
     */
    @Transaction
    @Query("SELECT * FROM transactions WHERE id = :id")
    fun getTransactionWithCategoryById(id: Long): Flow<TransactionWithCategory>

    /**
     * Gets all transactions within a specific date range, ordered by date descending.
     *
     * @param startDate The start timestamp of the date range (inclusive)
     * @param endDate The end timestamp of the date range (inclusive)
     * @return Flow emitting a list of transaction entities within the date range
     */
    @Query("SELECT * FROM transactions WHERE date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    fun getTransactionsByDateRange(startDate: Long, endDate: Long): Flow<List<TransactionEntity>>

    /**
     * Gets all transactions with their associated categories within a specific date range,
     * ordered by date descending.
     *
     * @param startDate The start timestamp of the date range (inclusive)
     * @param endDate The end timestamp of the date range (exclusive)
     * @return Flow emitting a list of TransactionWithCategory objects within the date range
     */
    @Transaction
    @Query("SELECT * FROM transactions WHERE date >= :startDate AND date < :endDate ORDER BY date DESC")
    fun getTransactionsWithCategoryByDateRange(
        startDate: Long,
        endDate: Long
    ): Flow<List<TransactionWithCategory>>

    /**
     * Gets all transactions for a specific category, ordered by date descending.
     *
     * @param categoryId The ID of the category to filter by
     * @return Flow emitting a list of transaction entities for the specified category
     */
    @Query("SELECT * FROM transactions WHERE categoryId = :categoryId ORDER BY date DESC")
    fun getTransactionsByCategory(categoryId: Long): Flow<List<TransactionEntity>>

    /**
     * Calculates the total amount of expenses within a specific date range.
     *
     * @param startDate The start timestamp of the date range (inclusive)
     * @param endDate The end timestamp of the date range (inclusive)
     * @return Flow emitting the total expenses amount, or null if no expenses found
     */
    @Query("SELECT SUM(amount) FROM transactions WHERE isExpense = 1 AND date BETWEEN :startDate AND :endDate")
    fun getTotalExpensesByDateRange(startDate: Long, endDate: Long): Flow<Double?>

    /**
     * Calculates the total amount of income within a specific date range.
     *
     * @param startDate The start timestamp of the date range (inclusive)
     * @param endDate The end timestamp of the date range (inclusive)
     * @return Flow emitting the total income amount, or null if no income found
     */
    @Query("SELECT SUM(amount) FROM transactions WHERE isExpense = 0 AND date BETWEEN :startDate AND :endDate")
    fun getTotalIncomeByDateRange(startDate: Long, endDate: Long): Flow<Double?>

    /**
     * Calculates the total amount of expenses for a specific category within a specific date range.
     *
     * @param categoryId The ID of the category to filter by
     * @param startDate The start timestamp of the date range (inclusive)
     * @param endDate The end timestamp of the date range (inclusive)
     * @return Flow emitting the total expenses amount for the category, or null if no expenses found
     */
    @Query("SELECT SUM(amount) FROM transactions WHERE categoryId = :categoryId AND isExpense = 1 AND date BETWEEN :startDate AND :endDate")
    fun getTotalExpensesByCategoryAndDateRange(
        categoryId: Long,
        startDate: Long,
        endDate: Long
    ): Flow<Double?>

    /**
     * Synchronously calculates the total amount of expenses for a specific category within a specific date range.
     *
     * @param categoryId The ID of the category to filter by
     * @param startDate The start timestamp of the date range (inclusive)
     * @param endDate The end timestamp of the date range (inclusive)
     * @return The total expenses amount for the category, or null if no expenses found
     */
    @Query("SELECT SUM(amount) FROM transactions WHERE categoryId = :categoryId AND isExpense = 1 AND date BETWEEN :startDate AND :endDate")
    suspend fun getTotalExpensesByCategoryAndDateRangeSync(
        categoryId: Long,
        startDate: Long,
        endDate: Long
    ): Double?

    /**
     * Synchronously counts the total number of transactions in the database.
     *
     * @return The count of transactions
     */
    @Query("SELECT COUNT(*) FROM transactions")
    suspend fun getTransactionCountSync(): Int

    // ==========================================
    // SYNC METHODS
    // ==========================================

    /**
     * Synchronously calculates the total amount of income.
     * This method is used by TransactionRepository.getTotalIncomeSync().
     *
     * @return The total income amount
     */
    @Query("SELECT SUM(amount) FROM transactions WHERE isExpense = 0")
    suspend fun getTotalIncomeSync(): Double

    /**
     * Synchronously calculates the total amount of expenses.
     * This method is used by TransactionRepository.getTotalExpensesSync().
     *
     * @return The total expenses amount
     */
    @Query("SELECT SUM(amount) FROM transactions WHERE isExpense = 1")
    suspend fun getTotalExpensesSync(): Double

    // ==========================================
    // CRUD OPERATIONS
    // ==========================================

    /**
     * Inserts a transaction into the database.
     * If a transaction with the same ID already exists, it is replaced.
     *
     * @param transaction The transaction entity to insert
     * @return The ID of the inserted transaction
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: TransactionEntity): Long

    /**
     * Updates an existing transaction in the database.
     *
     * @param transaction The transaction entity to update
     */
    @Update
    suspend fun updateTransaction(transaction: TransactionEntity)

    /**
     * Deletes a transaction from the database.
     *
     * @param transaction The transaction entity to delete
     */
    @Delete
    suspend fun deleteTransaction(transaction: TransactionEntity)

    /**
     * Inserts a list of transactions into the database.
     * If transactions with the same IDs already exist, they are replaced.
     *
     * @param transactions The list of transaction entities to insert
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(transactions: List<TransactionEntity>)

    // ==========================================
    // DATABASE REPAIR METHODS
    // ==========================================

    /**
     * Finds all transactions with invalid category references.
     * These transactions refer to categories that don't exist.
     *
     * @return List of transaction entities with invalid category references
     */
    @Query(
        """
        SELECT * FROM transactions 
        WHERE categoryId NOT IN (SELECT id FROM categories)
    """
    )
    suspend fun getTransactionsWithInvalidCategories(): List<TransactionEntity>

    /**
     * Updates all transactions with invalid categoryId
     * and assigns them a valid default category.
     *
     * @param defaultCategoryId The ID of the default category to assign
     */
    @Query(
        """
        UPDATE transactions 
        SET categoryId = :defaultCategoryId 
        WHERE categoryId NOT IN (SELECT id FROM categories)
    """
    )
    suspend fun updateCategoryIdForOrphanedTransactions(defaultCategoryId: Long)

    /**
     * Checks if transactions with invalid category references exist.
     *
     * @return The count of transactions with invalid category references
     */
    @Query(
        """
        SELECT COUNT(*) FROM transactions 
        WHERE categoryId NOT IN (SELECT id FROM categories)
    """
    )
    suspend fun countTransactionsWithInvalidCategories(): Int
}
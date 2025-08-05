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

@Dao
interface TransactionDao {
    @Query("SELECT * FROM transactions ORDER BY date DESC")
    fun getAllTransactions(): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE id = :id")
    fun getTransactionById(id: Long): Flow<TransactionEntity>

    @Transaction
    @Query("SELECT * FROM transactions ORDER BY date DESC")
    fun getTransactionsWithCategory(): Flow<List<TransactionWithCategory>>

    @Transaction
    @Query("SELECT * FROM transactions WHERE id = :id")
    fun getTransactionWithCategoryById(id: Long): Flow<TransactionWithCategory>

    @Query("SELECT * FROM transactions WHERE date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    fun getTransactionsByDateRange(startDate: Long, endDate: Long): Flow<List<TransactionEntity>>

    @Transaction
    @Query("SELECT * FROM transactions WHERE date >= :startDate AND date < :endDate ORDER BY date DESC")
    fun getTransactionsWithCategoryByDateRange(
        startDate: Long,
        endDate: Long
    ): Flow<List<TransactionWithCategory>>

    @Query("SELECT * FROM transactions WHERE categoryId = :categoryId ORDER BY date DESC")
    fun getTransactionsByCategory(categoryId: Long): Flow<List<TransactionEntity>>

    @Query("SELECT SUM(amount) FROM transactions WHERE isExpense = 1 AND date BETWEEN :startDate AND :endDate")
    fun getTotalExpensesByDateRange(startDate: Long, endDate: Long): Flow<Double?>

    @Query("SELECT SUM(amount) FROM transactions WHERE isExpense = 0 AND date BETWEEN :startDate AND :endDate")
    fun getTotalIncomeByDateRange(startDate: Long, endDate: Long): Flow<Double?>

    @Query("SELECT SUM(amount) FROM transactions WHERE categoryId = :categoryId AND isExpense = 1 AND date BETWEEN :startDate AND :endDate")
    fun getTotalExpensesByCategoryAndDateRange(
        categoryId: Long,
        startDate: Long,
        endDate: Long
    ): Flow<Double?>

    @Query("SELECT SUM(amount) FROM transactions WHERE categoryId = :categoryId AND isExpense = 1 AND date BETWEEN :startDate AND :endDate")
    suspend fun getTotalExpensesByCategoryAndDateRangeSync(
        categoryId: Long,
        startDate: Long,
        endDate: Long
    ): Double?

    @Query("SELECT COUNT(*) FROM transactions")
    suspend fun getTransactionCountSync(): Int

    // ==========================================
    // SYNC METHODS (FEHLENDE METHODEN HINZUGEFÜGT)
    // ==========================================

    /**
     * Synchrone Berechnung der Gesamteinnahmen.
     * Diese Methode wird von TransactionRepository.getTotalIncomeSync() verwendet.
     */
    @Query("SELECT SUM(amount) FROM transactions WHERE isExpense = 0")
    suspend fun getTotalIncomeSync(): Double

    /**
     * Synchrone Berechnung der Gesamtausgaben.
     * Diese Methode wird von TransactionRepository.getTotalExpensesSync() verwendet.
     */
    @Query("SELECT SUM(amount) FROM transactions WHERE isExpense = 1")
    suspend fun getTotalExpensesSync(): Double

    // ==========================================
    // CRUD OPERATIONS
    // ==========================================

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: TransactionEntity): Long

    @Update
    suspend fun updateTransaction(transaction: TransactionEntity)

    @Delete
    suspend fun deleteTransaction(transaction: TransactionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(transactions: List<TransactionEntity>)

    // ==========================================
    // DATABASE REPAIR METHODS
    // ==========================================

    /**
     * Findet alle Transaktionen mit ungültigen Kategorie-Referenzen.
     * Diese Transaktionen verweisen auf Kategorien, die nicht existieren.
     */
    @Query("""
        SELECT * FROM transactions 
        WHERE categoryId NOT IN (SELECT id FROM categories)
    """)
    suspend fun getTransactionsWithInvalidCategories(): List<TransactionEntity>

    /**
     * Aktualisiert alle Transaktionen mit ungültigen categoryId
     * und weist ihnen eine gültige Standard-Kategorie zu.
     */
    @Query("""
        UPDATE transactions 
        SET categoryId = :defaultCategoryId 
        WHERE categoryId NOT IN (SELECT id FROM categories)
    """)
    suspend fun updateCategoryIdForOrphanedTransactions(defaultCategoryId: Long)

    /**
     * Prüft ob Transaktionen mit ungültigen Kategorie-Referenzen existieren.
     */
    @Query("""
        SELECT COUNT(*) FROM transactions 
        WHERE categoryId NOT IN (SELECT id FROM categories)
    """)
    suspend fun countTransactionsWithInvalidCategories(): Int
}
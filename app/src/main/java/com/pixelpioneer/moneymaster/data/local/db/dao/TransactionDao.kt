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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: TransactionEntity): Long

    @Update
    suspend fun updateTransaction(transaction: TransactionEntity)

    @Delete
    suspend fun deleteTransaction(transaction: TransactionEntity)

    @Query("SELECT COALESCE(SUM(amount), 0) FROM transactions WHERE isExpense = 0")
    suspend fun getTotalIncomeSync(): Double

    @Query("SELECT COALESCE(SUM(amount), 0) FROM transactions WHERE isExpense = 1")
    suspend fun getTotalExpensesSync(): Double

    @Query("SELECT COUNT(*) FROM transactions")
    suspend fun getTransactionCountSync(): Int
}
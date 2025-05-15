package com.pixelpioneer.moneymaster.data.repository

import com.pixelpioneer.moneymaster.data.db.TransactionDao
import com.pixelpioneer.moneymaster.data.mapper.TransactionMapper
import com.pixelpioneer.moneymaster.data.model.Transaction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Calendar

class TransactionRepository(private val transactionDao: TransactionDao) {
    
    val allTransactionsWithCategory = transactionDao.getTransactionsWithCategory()
        .map { list -> list.map { TransactionMapper.fromEntity(it) } }
    
    fun getTransactionById(id: Long): Flow<Transaction> {
        return transactionDao.getTransactionWithCategoryById(id)
            .map { TransactionMapper.fromEntity(it) }
    }
    
    suspend fun insertTransaction(transaction: Transaction): Long {
        val entity = TransactionMapper.toEntity(transaction)
        return transactionDao.insertTransaction(entity)
    }
    
    suspend fun updateTransaction(transaction: Transaction) {
        val entity = TransactionMapper.toEntity(transaction)
        transactionDao.updateTransaction(entity)
    }
    
    suspend fun deleteTransaction(transaction: Transaction) {
        val entity = TransactionMapper.toEntity(transaction)
        transactionDao.deleteTransaction(entity)
    }
    
    fun getTransactionsByDateRange(startDate: Long, endDate: Long): Flow<List<Transaction>> {
        return transactionDao.getTransactionsWithCategory()
            .map { list -> 
                list.filter { it.transaction.date in startDate..endDate }
                    .map { TransactionMapper.fromEntity(it) } 
            }
    }
    
    fun getCurrentMonthTransactions(): Flow<List<Transaction>> {
        val calendar = Calendar.getInstance()
        
        // Setze Kalender auf den ersten Tag des aktuellen Monats
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startDate = calendar.timeInMillis
        
        // Setze Kalender auf den ersten Tag des nächsten Monats
        calendar.add(Calendar.MONTH, 1)
        val endDate = calendar.timeInMillis
        
        return getTransactionsByDateRange(startDate, endDate)
    }
    
    fun getTotalExpensesByMonth(): Flow<Double> {
        val calendar = Calendar.getInstance()
        
        // Setze Kalender auf den ersten Tag des aktuellen Monats
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startDate = calendar.timeInMillis
        
        // Setze Kalender auf den ersten Tag des nächsten Monats
        calendar.add(Calendar.MONTH, 1)
        val endDate = calendar.timeInMillis
        
        return transactionDao.getTotalExpensesByDateRange(startDate, endDate)
            .map { it ?: 0.0 } // Null-Werte zu 0.0 konvertieren
    }
    
    fun getTotalIncomeByMonth(): Flow<Double> {
        val calendar = Calendar.getInstance()
        
        // Setze Kalender auf den ersten Tag des aktuellen Monats
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startDate = calendar.timeInMillis
        
        // Setze Kalender auf den ersten Tag des nächsten Monats
        calendar.add(Calendar.MONTH, 1)
        val endDate = calendar.timeInMillis
        
        return transactionDao.getTotalIncomeByDateRange(startDate, endDate)
            .map { it ?: 0.0 } // Null-Werte zu 0.0 konvertieren
    }
    
    fun getTotalExpensesByCategoryForCurrentMonth(categoryId: Long): Flow<Double> {
        val calendar = Calendar.getInstance()
        
        // Setze Kalender auf den ersten Tag des aktuellen Monats
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startDate = calendar.timeInMillis
        
        // Setze Kalender auf den ersten Tag des nächsten Monats
        calendar.add(Calendar.MONTH, 1)
        val endDate = calendar.timeInMillis
        
        return transactionDao.getTotalExpensesByCategoryAndDateRange(categoryId, startDate, endDate)
            .map { it ?: 0.0 } // Null-Werte zu 0.0 konvertieren
    }
}
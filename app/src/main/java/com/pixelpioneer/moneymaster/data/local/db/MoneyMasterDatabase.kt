package com.pixelpioneer.moneymaster.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.pixelpioneer.moneymaster.data.local.db.dao.BudgetDao
import com.pixelpioneer.moneymaster.data.local.db.dao.CategoryDao
import com.pixelpioneer.moneymaster.data.local.db.dao.TransactionDao
import com.pixelpioneer.moneymaster.data.local.entity.BudgetEntity
import com.pixelpioneer.moneymaster.data.local.entity.CategoryEntity
import com.pixelpioneer.moneymaster.data.local.entity.TransactionEntity

/**
 * Room Database for the Money Master application.
 * This database contains tables for transactions, categories, and budgets.
 */
@Database(
    entities = [
        TransactionEntity::class,
        CategoryEntity::class,
        BudgetEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class MoneyMasterDatabase : RoomDatabase() {

    /**
     * Provides access to the TransactionDao.
     *
     * @return The DAO for accessing transaction data
     */
    abstract fun transactionDao(): TransactionDao

    /**
     * Provides access to the CategoryDao.
     *
     * @return The DAO for accessing category data
     */
    abstract fun categoryDao(): CategoryDao

    /**
     * Provides access to the BudgetDao.
     *
     * @return The DAO for accessing budget data
     */
    abstract fun budgetDao(): BudgetDao
}
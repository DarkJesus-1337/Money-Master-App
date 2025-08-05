package com.pixelpioneer.moneymaster.data.local.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
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

    companion object {
        @Volatile
        private var INSTANCE: MoneyMasterDatabase? = null

        /**
         * Gets the singleton database instance.
         * If the instance doesn't exist yet, it creates a new database instance.
         *
         * @param context The application context
         * @return The singleton database instance
         */
        fun getDatabase(context: Context): MoneyMasterDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MoneyMasterDatabase::class.java,
                    "money_master_database"
                )
                    .fallbackToDestructiveMigration(false)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
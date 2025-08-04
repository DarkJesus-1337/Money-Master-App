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

    abstract fun transactionDao(): TransactionDao
    abstract fun categoryDao(): CategoryDao
    abstract fun budgetDao(): BudgetDao

    companion object {
        @Volatile
        private var INSTANCE: MoneyMasterDatabase? = null

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
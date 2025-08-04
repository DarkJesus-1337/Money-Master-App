package com.pixelpioneer.moneymaster.core.di

import android.content.Context
import androidx.room.Room
import com.pixelpioneer.moneymaster.data.local.db.MoneyMasterDatabase
import com.pixelpioneer.moneymaster.data.local.db.dao.BudgetDao
import com.pixelpioneer.moneymaster.data.local.db.dao.CategoryDao
import com.pixelpioneer.moneymaster.data.local.db.dao.TransactionDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideMoneyMasterDatabase(
        @ApplicationContext context: Context
    ): MoneyMasterDatabase {
        return Room.databaseBuilder(
            context,
            MoneyMasterDatabase::class.java,
            "money_master_database"
        ).build()
    }

    @Provides
    fun provideTransactionDao(database: MoneyMasterDatabase): TransactionDao {
        return database.transactionDao()
    }

    @Provides
    fun provideCategoryDao(database: MoneyMasterDatabase): CategoryDao {
        return database.categoryDao()
    }

    @Provides
    fun provideBudgetDao(database: MoneyMasterDatabase): BudgetDao {
        return database.budgetDao()
    }
}
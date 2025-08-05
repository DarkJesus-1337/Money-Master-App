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

/**
 * Hilt Module for Database and DAOs.
 * This module is only responsible for providing the database and DAOs.
 * The repositories are provided in the RepositoryModule.
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    /**
     * Provides the singleton instance of the MoneyMaster database.
     *
     * @param context The application context used to build the database.
     * @return An instance of [MoneyMasterDatabase].
     */
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

    /**
     * Provides the TransactionDao for accessing transaction data.
     *
     * @param database The database instance from which to get the DAO.
     * @return An instance of [TransactionDao].
     */
    @Provides
    fun provideTransactionDao(database: MoneyMasterDatabase): TransactionDao {
        return database.transactionDao()
    }

    /**
     * Provides the CategoryDao for accessing category data.
     *
     * @param database The database instance from which to get the DAO.
     * @return An instance of [CategoryDao].
     */
    @Provides
    fun provideCategoryDao(database: MoneyMasterDatabase): CategoryDao {
        return database.categoryDao()
    }

    /**
     * Provides the BudgetDao for accessing budget data.
     *
     * @param database The database instance from which to get the DAO.
     * @return An instance of [BudgetDao].
     */
    @Provides
    fun provideBudgetDao(database: MoneyMasterDatabase): BudgetDao {
        return database.budgetDao()
    }
}
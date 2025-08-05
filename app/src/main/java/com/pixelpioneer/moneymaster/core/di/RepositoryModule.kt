package com.pixelpioneer.moneymaster.core.di

import android.content.Context
import com.pixelpioneer.moneymaster.core.network.RemoteConfigManager
import com.pixelpioneer.moneymaster.data.local.db.dao.BudgetDao
import com.pixelpioneer.moneymaster.data.local.db.dao.CategoryDao
import com.pixelpioneer.moneymaster.data.local.db.dao.TransactionDao
import com.pixelpioneer.moneymaster.data.remote.api.CoinCapApiService
import com.pixelpioneer.moneymaster.data.repository.BudgetRepository
import com.pixelpioneer.moneymaster.data.repository.CategoryRepository
import com.pixelpioneer.moneymaster.data.repository.CoinCapRepository
import com.pixelpioneer.moneymaster.data.repository.ReceiptScanRepository
import com.pixelpioneer.moneymaster.data.repository.SettingsRepository
import com.pixelpioneer.moneymaster.data.repository.TransactionRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt Module for repository provision.
 * This module is only responsible for providing the repositories.
 * Database and DAOs are provided in the DatabaseModule.
 */
@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    /**
     * Provides a singleton instance of TransactionRepository.
     *
     * @param transactionDao Data access object for transactions.
     * @param coinCapApiService API service for cryptocurrency data.
     * @param context Application context for resource access.
     * @return An instance of [TransactionRepository].
     */
    @Provides
    @Singleton
    fun provideTransactionRepository(
        transactionDao: TransactionDao,
        coinCapApiService: CoinCapApiService,
        @ApplicationContext context: Context
    ): TransactionRepository {
        return TransactionRepository(
            transactionDao,
            coinCapApiService,
            context
        )
    }

    /**
     * Provides a singleton instance of CategoryRepository.
     *
     * @param categoryDao Data access object for categories.
     * @param transactionDao Data access object for transactions.
     * @param context Application context for resource access.
     * @return An instance of [CategoryRepository].
     */
    @Provides
    @Singleton
    fun provideCategoryRepository(
        categoryDao: CategoryDao,
        transactionDao: TransactionDao,
        @ApplicationContext context: Context
    ): CategoryRepository {
        return CategoryRepository(categoryDao, transactionDao, context)
    }

    /**
     * Provides a singleton instance of BudgetRepository.
     *
     * @param budgetDao Data access object for budgets.
     * @param transactionDao Data access object for transactions.
     * @return An instance of [BudgetRepository].
     */
    @Provides
    @Singleton
    fun provideBudgetRepository(
        budgetDao: BudgetDao,
        transactionDao: TransactionDao
    ): BudgetRepository {
        return BudgetRepository(budgetDao, transactionDao)
    }

    /**
     * Provides a singleton instance of CoinCapRepository.
     *
     * @param coinCapApiService API service for cryptocurrency data.
     * @param context Application context for resource access.
     * @return An instance of [CoinCapRepository].
     */
    @Provides
    @Singleton
    fun provideCoinCapRepository(
        coinCapApiService: CoinCapApiService,
        @ApplicationContext context: Context
    ): CoinCapRepository {
        return CoinCapRepository(
            coinCapApiService,
            context
        )
    }

    /**
     * Provides a singleton instance of ReceiptScanRepository.
     *
     * @param remoteConfigManager Manager for remote configuration.
     * @param context Application context for resource access.
     * @return An instance of [ReceiptScanRepository].
     */
    @Provides
    @Singleton
    fun provideReceiptScanRepository(
        remoteConfigManager: RemoteConfigManager,
        @ApplicationContext context: Context
    ): ReceiptScanRepository {
        return ReceiptScanRepository(
            remoteConfigManager,
            context
        )
    }

    /**
     * Provides a singleton instance of SettingsRepository.
     *
     * @param context Application context for resource access.
     * @return An instance of [SettingsRepository].
     */
    @Provides
    @Singleton
    fun provideSettingsRepository(
        @ApplicationContext context: Context
    ): SettingsRepository {
        return SettingsRepository(context)
    }
}
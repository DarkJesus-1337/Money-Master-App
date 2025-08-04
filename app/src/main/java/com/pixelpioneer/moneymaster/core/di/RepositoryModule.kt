package com.pixelpioneer.moneymaster.core.di

import android.content.Context
import com.pixelpioneer.moneymaster.data.db.dao.BudgetDao
import com.pixelpioneer.moneymaster.data.db.dao.CategoryDao
import com.pixelpioneer.moneymaster.data.db.dao.TransactionDao
import com.pixelpioneer.moneymaster.data.repository.BudgetRepository
import com.pixelpioneer.moneymaster.data.repository.CategoryRepository
import com.pixelpioneer.moneymaster.data.repository.CoinCapRepository
import com.pixelpioneer.moneymaster.data.repository.ReceiptScanRepository
import com.pixelpioneer.moneymaster.data.repository.SettingsRepository
import com.pixelpioneer.moneymaster.data.repository.TransactionRepository
import com.pixelpioneer.moneymaster.data.services.CoinCapApiService
import com.pixelpioneer.moneymaster.data.services.RemoteConfigManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

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

    @Provides
    @Singleton
    fun provideCategoryRepository(
        categoryDao: CategoryDao
    ): CategoryRepository {
        return CategoryRepository(categoryDao)
    }

    @Provides
    @Singleton
    fun provideBudgetRepository(
        budgetDao: BudgetDao,
        transactionDao: TransactionDao
    ): BudgetRepository {
        return BudgetRepository(budgetDao, transactionDao)
    }

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

    @Provides
    @Singleton
    fun provideSettingsRepository(
        @ApplicationContext context: Context
    ): SettingsRepository {
        return SettingsRepository(context)
    }
}
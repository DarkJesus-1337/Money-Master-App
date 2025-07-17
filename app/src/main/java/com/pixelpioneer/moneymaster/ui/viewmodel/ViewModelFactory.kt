package com.pixelpioneer.moneymaster.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.pixelpioneer.moneymaster.data.repository.BudgetRepository
import com.pixelpioneer.moneymaster.data.repository.CategoryRepository
import com.pixelpioneer.moneymaster.data.repository.CoinCapRepository
import com.pixelpioneer.moneymaster.data.repository.ReceiptScanRepository
import com.pixelpioneer.moneymaster.data.repository.TransactionRepository
import com.pixelpioneer.moneymaster.data.services.RemoteConfigManager

/**
 * Factory for creating ViewModel instances with required dependencies.
 *
 * Provides ViewModels for transactions, categories, budgets, statistics,
 * cryptocurrencies, receipt scanning, and remote config.
 *
 * @property transactionRepository Repository for transaction data.
 * @property categoryRepository Repository for category data.
 * @property budgetRepository Repository for budget data.
 * @property coinCapRepository Repository for cryptocurrency data.
 * @property receiptScanRepository Repository for receipt scanning.
 * @property remoteConfigManager Manager for remote configuration.
 */
class ViewModelFactory(
    private val transactionRepository: TransactionRepository,
    private val categoryRepository: CategoryRepository,
    private val budgetRepository: BudgetRepository,
    private val coinCapRepository: CoinCapRepository,
    private val receiptScanRepository: ReceiptScanRepository,
    private val remoteConfigManager: RemoteConfigManager,
    private val context: Context
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(TransactionViewModel::class.java) -> {
                TransactionViewModel(transactionRepository, categoryRepository, context) as T
            }

            modelClass.isAssignableFrom(CategoryViewModel::class.java) -> {
                CategoryViewModel(categoryRepository, context) as T
            }

            modelClass.isAssignableFrom(BudgetViewModel::class.java) -> {
                BudgetViewModel(budgetRepository, categoryRepository, context) as T
            }

            modelClass.isAssignableFrom(StatisticsViewModel::class.java) -> {
                StatisticsViewModel(transactionRepository, categoryRepository, context) as T
            }

            modelClass.isAssignableFrom(CryptoViewModel::class.java) -> {
                CryptoViewModel(coinCapRepository, context) as T
            }

            modelClass.isAssignableFrom(ReceiptScanViewModel::class.java) -> {
                ReceiptScanViewModel(receiptScanRepository, context) as T
            }

            modelClass.isAssignableFrom(RemoteConfigViewModel::class.java) -> {
                RemoteConfigViewModel(remoteConfigManager, context) as T
            }

            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
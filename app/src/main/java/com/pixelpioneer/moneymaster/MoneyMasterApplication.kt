package com.pixelpioneer.moneymaster

import android.app.Application
import androidx.lifecycle.ViewModelProvider
import com.pixelpioneer.moneymaster.data.db.MoneyMasterDatabase
import com.pixelpioneer.moneymaster.data.repository.BudgetRepository
import com.pixelpioneer.moneymaster.data.repository.CategoryRepository
import com.pixelpioneer.moneymaster.data.repository.TransactionRepository
import com.pixelpioneer.moneymaster.ui.viewmodel.ViewModelFactory

class MoneyMasterApplication : Application() {
    
    // Lazy database initialization
    private val database by lazy {
        MoneyMasterDatabase.getDatabase(this)
    }
    
    // Lazy repository initialization
    val transactionRepository by lazy {
        TransactionRepository(database.transactionDao())
    }
    
    val categoryRepository by lazy {
        CategoryRepository(database.categoryDao())
    }
    
    val budgetRepository by lazy {
        BudgetRepository(database.budgetDao(), database.transactionDao())
    }
    
    // ViewModel factory for providing ViewModels with dependencies
    val viewModelFactory by lazy {
        ViewModelFactory(
            transactionRepository = transactionRepository,
            categoryRepository = categoryRepository,
            budgetRepository = budgetRepository
        )
    }
    
    override fun onCreate() {
        super.onCreate()
        
        // Initialize application-wide components here
    }
}
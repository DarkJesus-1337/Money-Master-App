package com.pixelpioneer.moneymaster

import android.app.Application
import com.pixelpioneer.moneymaster.data.db.MoneyMasterDatabase
import com.pixelpioneer.moneymaster.data.repository.BudgetRepository
import com.pixelpioneer.moneymaster.data.repository.CategoryRepository
import com.pixelpioneer.moneymaster.data.repository.TransactionRepository
import com.pixelpioneer.moneymaster.ui.viewmodel.ViewModelFactory

class MoneyMasterApplication : Application() {
    
    private val database by lazy {
        MoneyMasterDatabase.getDatabase(this)
    }
    
    private val transactionRepository by lazy {
        TransactionRepository(database.transactionDao())
    }
    
    private val categoryRepository by lazy {
        CategoryRepository(database.categoryDao())
    }
    
    private val budgetRepository by lazy {
        BudgetRepository(database.budgetDao(), database.transactionDao())
    }
    
    val viewModelFactory by lazy {
        ViewModelFactory(
            transactionRepository = transactionRepository,
            categoryRepository = categoryRepository,
            budgetRepository = budgetRepository
        )
    }

}
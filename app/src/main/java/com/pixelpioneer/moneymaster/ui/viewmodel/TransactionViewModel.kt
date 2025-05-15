package com.pixelpioneer.moneymaster.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pixelpioneer.moneymaster.data.model.Transaction
import com.pixelpioneer.moneymaster.data.model.TransactionCategory
import com.pixelpioneer.moneymaster.data.repository.CategoryRepository
import com.pixelpioneer.moneymaster.data.repository.TransactionRepository
import com.pixelpioneer.moneymaster.util.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import java.util.Calendar

class TransactionViewModel(
    private val transactionRepository: TransactionRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    // UI state for transactions list
    private val _transactionsState = MutableStateFlow<UiState<List<Transaction>>>(UiState.Loading)
    val transactionsState: StateFlow<UiState<List<Transaction>>> = _transactionsState

    // UI state for categories (needed for transaction creation)
    private val _categoriesState = MutableStateFlow<UiState<List<TransactionCategory>>>(UiState.Loading)
    val categoriesState: StateFlow<UiState<List<TransactionCategory>>> = _categoriesState

    // UI state for a single transaction (for detail view)
    private val _selectedTransaction = MutableStateFlow<UiState<Transaction>>(UiState.Loading)
    val selectedTransaction: StateFlow<UiState<Transaction>> = _selectedTransaction

    // Form state for adding/editing a transaction
    private val _transactionFormState = MutableStateFlow(TransactionFormState())
    val transactionFormState: StateFlow<TransactionFormState> = _transactionFormState

    // Financial summary for the dashboard
    private val _financialSummary = MutableStateFlow<UiState<FinancialSummary>>(UiState.Loading)
    val financialSummary: StateFlow<UiState<FinancialSummary>> = _financialSummary

    init {
        loadTransactions()
        loadCategories()
        loadFinancialSummary()
    }

    // Load all transactions
    private fun loadTransactions() {
        viewModelScope.launch {
            try {
                _transactionsState.value = UiState.Loading
                transactionRepository.allTransactionsWithCategory
                    .catch { e ->
                        _transactionsState.value = UiState.Error(e.message ?: "Unknown error occurred")
                    }
                    .collect { transactions ->
                        if (transactions.isEmpty()) {
                            _transactionsState.value = UiState.Empty
                        } else {
                            _transactionsState.value = UiState.Success(transactions)
                        }
                    }
            } catch (e: Exception) {
                _transactionsState.value = UiState.Error(e.message ?: "Unknown error occurred")
            }
        }
    }

    // Load a specific transaction by ID
    fun loadTransactionById(id: Long) {
        viewModelScope.launch {
            try {
                _selectedTransaction.value = UiState.Loading
                transactionRepository.getTransactionById(id)
                    .catch { e ->
                        _selectedTransaction.value = UiState.Error(e.message ?: "Unknown error occurred")
                    }
                    .collect { transaction ->
                        _selectedTransaction.value = UiState.Success(transaction)
                    }
            } catch (e: Exception) {
                _selectedTransaction.value = UiState.Error(e.message ?: "Unknown error occurred")
            }
        }
    }

    // Load all categories (for transaction creation)
    private fun loadCategories() {
        viewModelScope.launch {
            try {
                _categoriesState.value = UiState.Loading
                categoryRepository.allCategories
                    .catch { e ->
                        _categoriesState.value = UiState.Error(e.message ?: "Unknown error occurred")
                    }
                    .collect { categories ->
                        if (categories.isEmpty()) {
                            _categoriesState.value = UiState.Empty
                        } else {
                            _categoriesState.value = UiState.Success(categories)
                        }
                    }
            } catch (e: Exception) {
                _categoriesState.value = UiState.Error(e.message ?: "Unknown error occurred")
            }
        }
    }

    // Load financial summary for the dashboard
    private fun loadFinancialSummary() {
        viewModelScope.launch {
            try {
                _financialSummary.value = UiState.Loading
                
                // Get current month's expenses and income
                transactionRepository.getTotalExpensesByMonth().collect { expenses ->
                    transactionRepository.getTotalIncomeByMonth().collect { income ->
                        val balance = income - expenses
                        
                        _financialSummary.value = UiState.Success(
                            FinancialSummary(
                                totalIncome = income,
                                totalExpenses = expenses,
                                balance = balance
                            )
                        )
                    }
                }
            } catch (e: Exception) {
                _financialSummary.value = UiState.Error(e.message ?: "Unknown error occurred")
            }
        }
    }

    // Create a new transaction
    fun createTransaction() {
        viewModelScope.launch {
            val formState = _transactionFormState.value
            
            // Validate form state
            if (!validateTransactionForm()) {
                return@launch
            }
            
            try {
                // Create a new Transaction object from form state
                val category = _transactionFormState.value.selectedCategory
                    ?: throw IllegalStateException("Category cannot be null")
                
                val transaction = Transaction(
                    amount = formState.amount,
                    title = formState.title,
                    description = formState.description,
                    category = category,
                    date = formState.date,
                    isExpense = formState.isExpense
                )
                
                // Insert the transaction
                transactionRepository.insertTransaction(transaction)
                
                // Reset form state
                resetFormState()
                
                // Reload transactions and financial summary
                loadTransactions()
                loadFinancialSummary()
            } catch (e: Exception) {
                // Handle error (could update a form error state here)
            }
        }
    }

    // Update an existing transaction
    fun updateTransaction(id: Long) {
        viewModelScope.launch {
            val formState = _transactionFormState.value
            
            // Validate form state
            if (!validateTransactionForm()) {
                return@launch
            }
            
            try {
                // Create a Transaction object from form state with the existing ID
                val category = _transactionFormState.value.selectedCategory
                    ?: throw IllegalStateException("Category cannot be null")
                
                val transaction = Transaction(
                    id = id,
                    amount = formState.amount,
                    title = formState.title,
                    description = formState.description,
                    category = category,
                    date = formState.date,
                    isExpense = formState.isExpense
                )
                
                // Update the transaction
                transactionRepository.updateTransaction(transaction)
                
                // Reset form state
                resetFormState()
                
                // Reload transactions and financial summary
                loadTransactions()
                loadFinancialSummary()
                
                // Also reload the selected transaction if it's being viewed
                loadTransactionById(id)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    // Delete a transaction
    fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch {
            try {
                transactionRepository.deleteTransaction(transaction)
                loadTransactions()
                loadFinancialSummary()
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    // Update form state when user changes form fields
    fun updateAmount(amount: Double) {
        _transactionFormState.value = _transactionFormState.value.copy(
            amount = amount,
            amountError = if (amount <= 0) "Amount must be greater than zero" else null
        )
    }

    fun updateTitle(title: String) {
        _transactionFormState.value = _transactionFormState.value.copy(
            title = title,
            titleError = if (title.isBlank()) "Title cannot be empty" else null
        )
    }

    fun updateDescription(description: String) {
        _transactionFormState.value = _transactionFormState.value.copy(description = description)
    }

    fun updateSelectedCategory(category: TransactionCategory) {
        _transactionFormState.value = _transactionFormState.value.copy(
            selectedCategory = category,
            categoryError = null
        )
    }

    fun updateDate(date: Long) {
        _transactionFormState.value = _transactionFormState.value.copy(date = date)
    }

    fun updateIsExpense(isExpense: Boolean) {
        _transactionFormState.value = _transactionFormState.value.copy(isExpense = isExpense)
    }

    // Reset form state
    fun resetFormState() {
        _transactionFormState.value = TransactionFormState()
    }

    // Initialize form state with an existing transaction (for editing)
    fun initFormWithTransaction(transaction: Transaction) {
        _transactionFormState.value = TransactionFormState(
            amount = transaction.amount,
            title = transaction.title,
            description = transaction.description,
            selectedCategory = transaction.category,
            date = transaction.date,
            isExpense = transaction.isExpense
        )
    }

    // Validate the transaction form
    private fun validateTransactionForm(): Boolean {
        val formState = _transactionFormState.value
        var isValid = true
        
        // Copy the current form state to modify with validation errors
        var updatedFormState = formState
        
        // Check amount
        if (formState.amount <= 0) {
            updatedFormState = updatedFormState.copy(
                amountError = "Amount must be greater than zero"
            )
            isValid = false
        }
        
        // Check title
        if (formState.title.isBlank()) {
            updatedFormState = updatedFormState.copy(
                titleError = "Title cannot be empty"
            )
            isValid = false
        }
        
        // Check category
        if (formState.selectedCategory == null) {
            updatedFormState = updatedFormState.copy(
                categoryError = "Please select a category"
            )
            isValid = false
        }
        
        // Update the form state with any validation errors
        _transactionFormState.value = updatedFormState
        
        return isValid
    }
}

// Form state for transaction creation/editing
data class TransactionFormState(
    val amount: Double = 0.0,
    val title: String = "",
    val description: String = "",
    val selectedCategory: TransactionCategory? = null,
    val date: Long = Calendar.getInstance().timeInMillis,
    val isExpense: Boolean = true,
    val amountError: String? = null,
    val titleError: String? = null,
    val categoryError: String? = null
)

// Financial summary for the dashboard
data class FinancialSummary(
    val totalIncome: Double = 0.0,
    val totalExpenses: Double = 0.0,
    val balance: Double = 0.0
)
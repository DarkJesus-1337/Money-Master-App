package com.pixelpioneer.moneymaster.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pixelpioneer.moneymaster.R
import com.pixelpioneer.moneymaster.data.model.Receipt
import com.pixelpioneer.moneymaster.data.model.Transaction
import com.pixelpioneer.moneymaster.data.model.TransactionCategory
import com.pixelpioneer.moneymaster.data.repository.CategoryRepository
import com.pixelpioneer.moneymaster.data.repository.TransactionRepository
import com.pixelpioneer.moneymaster.util.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

/**
 * ViewModel for managing transactions and related financial data.
 *
 * Handles loading, creating, updating, and deleting transactions,
 * as well as managing categories, form state, and financial summaries.
 * Provides UI state flows for transactions and summaries.
 *
 * @property transactionRepository Repository for transaction data access.
 * @property categoryRepository Repository for category data access.
 */
@HiltViewModel
class TransactionViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val categoryRepository: CategoryRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {
    private val _transactionsState = MutableStateFlow<UiState<List<Transaction>>>(UiState.Loading)
    val transactionsState: StateFlow<UiState<List<Transaction>>> = _transactionsState

    private val _categoriesState =
        MutableStateFlow<UiState<List<TransactionCategory>>>(UiState.Loading)
    val categoriesState: StateFlow<UiState<List<TransactionCategory>>> = _categoriesState

    private val _selectedTransaction = MutableStateFlow<UiState<Transaction>>(UiState.Loading)
    val selectedTransaction: StateFlow<UiState<Transaction>> = _selectedTransaction

    private val _transactionFormState = MutableStateFlow(TransactionFormState())
    val transactionFormState: StateFlow<TransactionFormState> = _transactionFormState

    private val _financialSummary = MutableStateFlow<UiState<FinancialSummary>>(UiState.Loading)
    val financialSummary: StateFlow<UiState<FinancialSummary>> = _financialSummary

    init {
        loadTransactions()
        loadCategories()
        loadFinancialSummary()
    }

    private fun loadTransactions() {
        viewModelScope.launch {
            try {
                _transactionsState.value = UiState.Loading
                transactionRepository.allTransactionsWithCategory
                    .catch { e ->
                        _transactionsState.value =
                            UiState.Error(e.message ?: context.getString(R.string.error_unknown))
                    }
                    .collect { transactions ->
                        if (transactions.isEmpty()) {
                            _transactionsState.value = UiState.Empty
                        } else {
                            _transactionsState.value = UiState.Success(transactions)
                        }
                    }
            } catch (e: Exception) {
                _transactionsState.value =
                    UiState.Error(e.message ?: context.getString(R.string.error_unknown))
            }
        }
    }

    fun loadTransactionById(id: Long) {
        viewModelScope.launch {
            try {
                _selectedTransaction.value = UiState.Loading
                transactionRepository.getTransactionById(id)
                    .catch { e ->
                        _selectedTransaction.value =
                            UiState.Error(e.message ?: context.getString(R.string.error_unknown))
                    }
                    .collect { transaction ->
                        _selectedTransaction.value = UiState.Success(transaction)
                    }
            } catch (e: Exception) {
                _selectedTransaction.value =
                    UiState.Error(e.message ?: context.getString(R.string.error_unknown))
            }
        }
    }

    private fun loadCategories() {
        viewModelScope.launch {
            try {
                _categoriesState.value = UiState.Loading
                categoryRepository.allCategories
                    .catch { e ->
                        _categoriesState.value =
                            UiState.Error(e.message ?: context.getString(R.string.error_unknown))
                    }
                    .collect { categories ->
                        if (categories.isEmpty()) {
                            _categoriesState.value = UiState.Empty
                        } else {
                            _categoriesState.value = UiState.Success(categories)
                        }
                    }
            } catch (e: Exception) {
                _categoriesState.value =
                    UiState.Error(e.message ?: context.getString(R.string.error_unknown))
            }
        }
    }

    private fun loadFinancialSummary() {
        viewModelScope.launch {
            try {
                _financialSummary.value = UiState.Loading

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
                _financialSummary.value =
                    UiState.Error(e.message ?: context.getString(R.string.error_unknown))
            }
        }
    }

    fun createTransaction() {
        viewModelScope.launch {
            val formState = _transactionFormState.value

            if (!validateTransactionForm()) {
                return@launch
            }

            try {
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

                transactionRepository.insertTransaction(transaction)

                resetFormState()

                loadTransactions()
                loadFinancialSummary()
            } catch (e: Exception) {
            }
        }
    }

    fun updateTransaction(id: Long) {
        viewModelScope.launch {
            val formState = _transactionFormState.value

            if (!validateTransactionForm()) {
                return@launch
            }

            try {
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

                transactionRepository.updateTransaction(transaction)

                resetFormState()

                loadTransactions()
                loadFinancialSummary()

                loadTransactionById(id)
            } catch (e: Exception) {
            }
        }
    }

    fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch {
            try {
                transactionRepository.deleteTransaction(transaction)
                loadTransactions()
                loadFinancialSummary()
            } catch (e: Exception) {
            }
        }
    }

    fun updateAmount(amount: Double) {
        _transactionFormState.value = _transactionFormState.value.copy(
            amount = amount,
            amountError = if (amount <= 0) context.getString(R.string.error_amount_greater_zero) else null
        )
    }

    fun updateTitle(title: String) {
        _transactionFormState.value = _transactionFormState.value.copy(
            title = title,
            titleError = if (title.isBlank()) context.getString(R.string.error_title_empty) else null
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

    private fun validateTransactionForm(): Boolean {
        val formState = _transactionFormState.value
        var isValid = true

        var updatedFormState = formState

        if (formState.amount <= 0) {
            updatedFormState = updatedFormState.copy(
                amountError = context.getString(R.string.error_amount_greater_zero)
            )
            isValid = false
        }

        if (formState.title.isBlank()) {
            updatedFormState = updatedFormState.copy(
                titleError = context.getString(R.string.error_title_empty)
            )
            isValid = false
        }

        if (formState.selectedCategory == null) {
            updatedFormState = updatedFormState.copy(
                categoryError = context.getString(R.string.error_select_category)
            )
            isValid = false
        }

        _transactionFormState.value = updatedFormState

        return isValid
    }

    fun saveReceiptAsTransactions(receipt: Receipt) {
        viewModelScope.launch {
            try {
                val defaultCategory = getDefaultCategory()

                receipt.items.forEach { item ->
                    val transaction = Transaction(
                        id = 0,
                        amount = item.price,
                        title = item.name,
                        description = context.getString(
                            R.string.transactions_from_receipt,
                            receipt.storeName ?: context.getString(R.string.common_unknown)
                        ),
                        category = defaultCategory,
                        date = System.currentTimeMillis(),
                        isExpense = true
                    )
                    transactionRepository.insertTransaction(transaction)
                }

                loadTransactions()
                loadFinancialSummary()

            } catch (e: Exception) {
            }
        }
    }

    private suspend fun getDefaultCategory(): TransactionCategory {
        return try {
            val categories = categoryRepository.allCategories.first()
            categories.firstOrNull()
                ?: TransactionCategory(
                    id = 1,
                    name = context.getString(R.string.category_shopping),
                    color = 0xFF4CAF50.toInt(),
                    icon = 0
                )
        } catch (e: Exception) {
            TransactionCategory(
                id = 1,
                name = context.getString(R.string.category_shopping),
                color = 0xFF4CAF50.toInt(),
                icon = 0
            )
        }
    }

    fun refreshFinancialSummary() {
        viewModelScope.launch {
            try {
                loadFinancialSummary()
            } catch (e: Exception) {
                // Handle error if needed
            }
        }
    }

    fun refreshTransactions() {
        loadTransactions()
    }

    fun addTransactionDirect(transaction: Transaction) {
        viewModelScope.launch {
            try {
                transactionRepository.insertTransaction(transaction)
                loadTransactions()
                loadFinancialSummary()
            } catch (e: Exception) {
            }
        }
    }

    fun resetFormState() {
        _transactionFormState.value = TransactionFormState()
    }
}


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

data class FinancialSummary(
    val totalIncome: Double = 0.0,
    val totalExpenses: Double = 0.0,
    val balance: Double = 0.0
)
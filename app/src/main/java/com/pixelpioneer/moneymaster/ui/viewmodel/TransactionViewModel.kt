package com.pixelpioneer.moneymaster.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pixelpioneer.moneymaster.R
import com.pixelpioneer.moneymaster.core.util.UiState
import com.pixelpioneer.moneymaster.data.model.FinancialSummary
import com.pixelpioneer.moneymaster.data.model.Transaction
import com.pixelpioneer.moneymaster.data.model.TransactionCategory
import com.pixelpioneer.moneymaster.data.model.TransactionFormState
import com.pixelpioneer.moneymaster.data.repository.CategoryRepository
import com.pixelpioneer.moneymaster.data.repository.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
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
 * @property context Application context for accessing resources.
 */
@HiltViewModel
class TransactionViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val categoryRepository: CategoryRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {
    /**
     * UI state for transaction list data.
     */
    private val _transactionsState = MutableStateFlow<UiState<List<Transaction>>>(UiState.Loading)

    /**
     * Public UI state flow for transaction list data.
     */
    val transactionsState: StateFlow<UiState<List<Transaction>>> = _transactionsState

    /**
     * UI state for category list data.
     */
    private val _categoriesState =
        MutableStateFlow<UiState<List<TransactionCategory>>>(UiState.Loading)

    /**
     * Public UI state flow for category list data.
     */
    val categoriesState: StateFlow<UiState<List<TransactionCategory>>> = _categoriesState

    /**
     * UI state for the currently selected transaction.
     */
    private val _selectedTransaction = MutableStateFlow<UiState<Transaction>>(UiState.Loading)

    /**
     * Public UI state flow for the currently selected transaction.
     */
    val selectedTransaction: StateFlow<UiState<Transaction>> = _selectedTransaction

    /**
     * Current state of the transaction form.
     */
    private val _transactionFormState = MutableStateFlow(TransactionFormState())

    /**
     * Public state flow for the transaction form.
     */
    val transactionFormState: StateFlow<TransactionFormState> = _transactionFormState

    /**
     * UI state for financial summary data.
     */
    private val _financialSummary = MutableStateFlow<UiState<FinancialSummary>>(UiState.Loading)

    /**
     * Public UI state flow for financial summary data.
     */
    val financialSummary: StateFlow<UiState<FinancialSummary>> = _financialSummary

    init {
        loadTransactions()
        loadCategories()
        loadFinancialSummary()
    }

    /**
     * Loads all transactions with their associated categories.
     */
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

    /**
     * Loads a specific transaction by its ID.
     *
     * @param id ID of the transaction to load
     */
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

    /**
     * Loads all available transaction categories.
     */
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

    /**
     * Loads financial summary data including total income, expenses, and balance.
     */
    private fun loadFinancialSummary() {
        viewModelScope.launch {
            try {
                _financialSummary.value = UiState.Loading

                combine(
                    transactionRepository.getTotalIncomeByMonth(),
                    transactionRepository.getTotalExpensesByMonth()
                ) { income, expenses ->
                    val balance = income - expenses
                    FinancialSummary(
                        totalIncome = income,
                        totalExpenses = expenses,
                        balance = balance
                    )
                }.catch { e ->
                    _financialSummary.value = UiState.Error(
                        e.message ?: context.getString(R.string.error_unknown)
                    )
                }.collect { summary ->
                    _financialSummary.value = UiState.Success(summary)
                }
            } catch (e: Exception) {
                _financialSummary.value = UiState.Error(
                    e.message ?: context.getString(R.string.error_unknown)
                )
            }
        }
    }
    
    /**
     * Creates a new transaction using the current form state.
     *
     * @param onComplete Callback to execute after transaction creation
     */
    fun createTransaction(onComplete: () -> Unit = {}) {
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
                onComplete()
            } catch (_: Exception) {
                // Error handling
            }
        }
    }


    /**
     * Updates an existing transaction with the current form state.
     *
     * @param id ID of the transaction to update
     */
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
            } catch (_: Exception) {
            }
        }
    }

    /**
     * Deletes a transaction.
     *
     * @param transaction The transaction to delete
     */
    fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch {
            try {
                transactionRepository.deleteTransaction(transaction)
                loadTransactions()
                loadFinancialSummary()
            } catch (_: Exception) {
            }
        }
    }

    /**
     * Updates the amount field in the transaction form.
     * Sets error if amount is invalid.
     *
     * @param amount The new amount value
     */
    fun updateAmount(amount: Double) {
        _transactionFormState.value = _transactionFormState.value.copy(
            amount = amount,
            amountError = if (amount <= 0) context.getString(R.string.error_amount_greater_zero) else null
        )
    }

    /**
     * Updates the title field in the transaction form.
     * Sets error if title is invalid.
     *
     * @param title The new title value
     */
    fun updateTitle(title: String) {
        _transactionFormState.value = _transactionFormState.value.copy(
            title = title,
            titleError = if (title.isBlank()) context.getString(R.string.error_title_empty) else null
        )
    }

    /**
     * Updates the description field in the transaction form.
     *
     * @param description The new description value
     */
    fun updateDescription(description: String) {
        _transactionFormState.value = _transactionFormState.value.copy(description = description)
    }

    /**
     * Updates the selected category in the transaction form.
     *
     * @param category The new selected category
     */
    fun updateSelectedCategory(category: TransactionCategory) {
        _transactionFormState.value = _transactionFormState.value.copy(
            selectedCategory = category,
            categoryError = null
        )
    }

    /**
     * Updates the date field in the transaction form.
     *
     * @param date The new date value in milliseconds
     */
    fun updateDate(date: Long) {
        _transactionFormState.value = _transactionFormState.value.copy(date = date)
    }

    /**
     * Updates whether the transaction is an expense or income.
     *
     * @param isExpense True if transaction is an expense, false if income
     */
    fun updateIsExpense(isExpense: Boolean) {
        _transactionFormState.value = _transactionFormState.value.copy(isExpense = isExpense)
    }

    /**
     * Initializes the form with data from an existing transaction.
     *
     * @param transaction The transaction to load into the form
     */
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

    /**
     * Validates the transaction form fields.
     * Updates error messages if validation fails.
     *
     * @return True if form is valid, false otherwise
     */
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

    /**
     * Refreshes the financial summary data.
     */
    fun refreshFinancialSummary() {
        viewModelScope.launch {
            try {
                loadFinancialSummary()
            } catch (_: Exception) {
                // Handle error
            }
        }
    }

    /**
     * Refreshes the transactions list.
     */
    fun refreshTransactions() {
        loadTransactions()
    }

    /**
     * Directly adds a fully formed transaction.
     *
     * @param transaction The transaction to add
     */
    fun addTransactionDirect(transaction: Transaction) {
        viewModelScope.launch {
            try {
                transactionRepository.insertTransaction(transaction)
                loadTransactions()
                loadFinancialSummary()
            } catch (_: Exception) {
            }
        }
    }

    /**
     * Resets the transaction form to its default state.
     */
    fun resetFormState() {
        _transactionFormState.value = TransactionFormState()
    }
}


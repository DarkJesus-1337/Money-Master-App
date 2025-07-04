package com.pixelpioneer.moneymaster.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pixelpioneer.moneymaster.data.enums.BudgetPeriod
import com.pixelpioneer.moneymaster.data.model.Budget
import com.pixelpioneer.moneymaster.data.model.TransactionCategory
import com.pixelpioneer.moneymaster.data.repository.BudgetRepository
import com.pixelpioneer.moneymaster.data.repository.CategoryRepository
import com.pixelpioneer.moneymaster.util.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class BudgetViewModel(
    private val budgetRepository: BudgetRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val _budgetsState = MutableStateFlow<UiState<List<Budget>>>(UiState.Loading)
    val budgetsState: StateFlow<UiState<List<Budget>>> = _budgetsState

    private val _categoriesState = MutableStateFlow<UiState<List<TransactionCategory>>>(UiState.Loading)
    val categoriesState: StateFlow<UiState<List<TransactionCategory>>> = _categoriesState

    private val _selectedBudget = MutableStateFlow<UiState<Budget>>(UiState.Loading)
    val selectedBudget: StateFlow<UiState<Budget>> = _selectedBudget

    private val _budgetFormState = MutableStateFlow(BudgetFormState())
    val budgetFormState: StateFlow<BudgetFormState> = _budgetFormState

    init {
        loadBudgets()
        loadCategories()
    }

    // Load all budgets with spent amounts
    private fun loadBudgets() {
        viewModelScope.launch {
            try {
                _budgetsState.value = UiState.Loading
                
                val budgets = budgetRepository.getBudgetsWithSpendingSync()

                if (budgets.isEmpty()) {
                    _budgetsState.value = UiState.Empty
                } else {
                    _budgetsState.value = UiState.Success(budgets)
                }
            } catch (e: Exception) {
                _budgetsState.value = UiState.Error(e.message ?: "Unknown error occurred")
            }
        }
    }

    private fun loadBudgetById(id: Long) {
        viewModelScope.launch {
            try {
                _selectedBudget.value = UiState.Loading
                budgetRepository.getBudgetById(id).collect { budget ->
                    budgetRepository.getSpentAmountForBudget(budget).collect { spentAmount ->
                        _selectedBudget.value = UiState.Success(budget.copy(spent = spentAmount))
                    }
                }
            } catch (e: Exception) {
                _selectedBudget.value = UiState.Error(e.message ?: "Unknown error occurred")
            }
        }
    }

    private fun loadCategories() {
        viewModelScope.launch {
            try {
                _categoriesState.value = UiState.Loading
                categoryRepository.allCategories.collect { categories ->
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

    fun createBudget() {
        viewModelScope.launch {
            val formState = _budgetFormState.value
            
            if (!validateBudgetForm()) {
                return@launch
            }
            
            try {
                val category = _budgetFormState.value.selectedCategory
                    ?: throw IllegalStateException("Category cannot be null")
                
                val budget = Budget(
                    category = category,
                    amount = formState.amount,
                    period = formState.period,
                    spent = 0.0
                )
                
                budgetRepository.insertBudget(budget)
                
                resetFormState()
                
                // Budgets werden automatisch durch den Flow aktualisiert
            } catch (e: Exception) {
                // Handle error (could update a form error state here)
            }
        }
    }

    fun updateBudget(id: Long) {
        viewModelScope.launch {
            val formState = _budgetFormState.value
            
            if (!validateBudgetForm()) {
                return@launch
            }
            
            try {
                val category = _budgetFormState.value.selectedCategory
                    ?: throw IllegalStateException("Category cannot be null")
                
                val budget = Budget(
                    id = id,
                    category = category,
                    amount = formState.amount,
                    period = formState.period,
                    spent = 0.0 // This value doesn't matter for update
                )
                
                budgetRepository.updateBudget(budget)
                
                resetFormState()
                
                loadBudgets()
                
                loadBudgetById(id)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun deleteBudget(budget: Budget) {
        viewModelScope.launch {
            try {
                budgetRepository.deleteBudget(budget)
                loadBudgets()
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun updateAmount(amount: Double) {
        _budgetFormState.value = _budgetFormState.value.copy(
            amount = amount,
            amountError = if (amount <= 0) "Amount must be greater than zero" else null
        )
    }

    fun updateSelectedCategory(category: TransactionCategory) {
        _budgetFormState.value = _budgetFormState.value.copy(
            selectedCategory = category,
            categoryError = null
        )
    }

    fun updatePeriod(period: BudgetPeriod) {
        _budgetFormState.value = _budgetFormState.value.copy(period = period)
    }

    private fun resetFormState() {
        _budgetFormState.value = BudgetFormState()
    }

    fun initFormWithBudget(budget: Budget) {
        _budgetFormState.value = BudgetFormState(
            amount = budget.amount,
            selectedCategory = budget.category,
            period = budget.period
        )
    }

    private fun validateBudgetForm(): Boolean {
        val formState = _budgetFormState.value
        
        if (formState.amount <= 0) {
            _budgetFormState.value = formState.copy(
                amountError = "Amount must be greater than zero"
            )
            return false
        }
        
        if (formState.selectedCategory == null) {
            _budgetFormState.value = formState.copy(
                categoryError = "Please select a category"
            )
            return false
        }
        
        return true
    }

    fun refreshBudgets() {
        loadBudgets()
    }
}

data class BudgetFormState(
    val amount: Double = 0.0,
    val selectedCategory: TransactionCategory? = null,
    val period: BudgetPeriod = BudgetPeriod.MONTHLY,
    val amountError: String? = null,
    val categoryError: String? = null
)
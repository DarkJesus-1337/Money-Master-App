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

    // UI state for the budget list
    private val _budgetsState = MutableStateFlow<UiState<List<Budget>>>(UiState.Loading)
    val budgetsState: StateFlow<UiState<List<Budget>>> = _budgetsState

    // UI state for categories (needed for budget creation)
    private val _categoriesState = MutableStateFlow<UiState<List<TransactionCategory>>>(UiState.Loading)
    val categoriesState: StateFlow<UiState<List<TransactionCategory>>> = _categoriesState

    // UI state for a single budget (for detail view)
    private val _selectedBudget = MutableStateFlow<UiState<Budget>>(UiState.Loading)
    val selectedBudget: StateFlow<UiState<Budget>> = _selectedBudget

    // Form state for adding/editing a budget
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
                // Get budgets and their spent amounts
                val budgetsFlow = budgetRepository.allBudgetsWithSpending
                _budgetsState.value = UiState.Loading
                
                // For each budget, get its spent amount
                budgetsFlow.collect { budgets ->
                    // Create a list to hold budgets with their spent amounts
                    val budgetsWithSpending = mutableListOf<Budget>()
                    
                    // Process each budget
                    for (budget in budgets) {
                        // Get spent amount for this budget
                        val spent = budgetRepository.getSpentAmountForBudget(budget).collect { spentAmount ->
                            // Add budget with spent amount to the list
                            budgetsWithSpending.add(budget.copy(spent = spentAmount))
                        }
                    }
                    
                    // Update UI state with the updated list of budgets
                    if (budgetsWithSpending.isEmpty()) {
                        _budgetsState.value = UiState.Empty
                    } else {
                        _budgetsState.value = UiState.Success(budgetsWithSpending)
                    }
                }
            } catch (e: Exception) {
                _budgetsState.value = UiState.Error(e.message ?: "Unknown error occurred")
            }
        }
    }

    // Load a specific budget by ID
    fun loadBudgetById(id: Long) {
        viewModelScope.launch {
            try {
                _selectedBudget.value = UiState.Loading
                budgetRepository.getBudgetById(id).collect { budget ->
                    // Get spent amount for this budget
                    budgetRepository.getSpentAmountForBudget(budget).collect { spentAmount ->
                        _selectedBudget.value = UiState.Success(budget.copy(spent = spentAmount))
                    }
                }
            } catch (e: Exception) {
                _selectedBudget.value = UiState.Error(e.message ?: "Unknown error occurred")
            }
        }
    }

    // Load all categories (for budget creation)
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

    // Create a new budget
    fun createBudget() {
        viewModelScope.launch {
            val formState = _budgetFormState.value
            
            // Validate form state
            if (!validateBudgetForm()) {
                return@launch
            }
            
            try {
                // Create a new Budget object from form state
                val category = _budgetFormState.value.selectedCategory
                    ?: throw IllegalStateException("Category cannot be null")
                
                val budget = Budget(
                    category = category,
                    amount = formState.amount,
                    period = formState.period,
                    spent = 0.0
                )
                
                // Insert the budget
                budgetRepository.insertBudget(budget)
                
                // Reset form state
                resetFormState()
                
                // Reload budgets
                loadBudgets()
            } catch (e: Exception) {
                // Handle error (could update a form error state here)
            }
        }
    }

    // Update an existing budget
    fun updateBudget(id: Long) {
        viewModelScope.launch {
            val formState = _budgetFormState.value
            
            // Validate form state
            if (!validateBudgetForm()) {
                return@launch
            }
            
            try {
                // Create a Budget object from form state with the existing ID
                val category = _budgetFormState.value.selectedCategory
                    ?: throw IllegalStateException("Category cannot be null")
                
                val budget = Budget(
                    id = id,
                    category = category,
                    amount = formState.amount,
                    period = formState.period,
                    spent = 0.0 // This value doesn't matter for update
                )
                
                // Update the budget
                budgetRepository.updateBudget(budget)
                
                // Reset form state
                resetFormState()
                
                // Reload budgets
                loadBudgets()
                
                // Also reload the selected budget if it's being viewed
                loadBudgetById(id)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    // Delete a budget
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

    // Update form state when user changes form fields
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

    // Reset form state
    fun resetFormState() {
        _budgetFormState.value = BudgetFormState()
    }

    // Initialize form state with an existing budget (for editing)
    fun initFormWithBudget(budget: Budget) {
        _budgetFormState.value = BudgetFormState(
            amount = budget.amount,
            selectedCategory = budget.category,
            period = budget.period
        )
    }

    // Validate the budget form
    private fun validateBudgetForm(): Boolean {
        val formState = _budgetFormState.value
        
        // Check amount
        if (formState.amount <= 0) {
            _budgetFormState.value = formState.copy(
                amountError = "Amount must be greater than zero"
            )
            return false
        }
        
        // Check category
        if (formState.selectedCategory == null) {
            _budgetFormState.value = formState.copy(
                categoryError = "Please select a category"
            )
            return false
        }
        
        return true
    }
}

// Form state for budget creation/editing
data class BudgetFormState(
    val amount: Double = 0.0,
    val selectedCategory: TransactionCategory? = null,
    val period: BudgetPeriod = BudgetPeriod.MONTHLY,
    val amountError: String? = null,
    val categoryError: String? = null
)
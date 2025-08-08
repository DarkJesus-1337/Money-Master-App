package com.pixelpioneer.moneymaster.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pixelpioneer.moneymaster.R
import com.pixelpioneer.moneymaster.core.util.UiState
import com.pixelpioneer.moneymaster.data.enums.BudgetPeriod
import com.pixelpioneer.moneymaster.data.model.Budget
import com.pixelpioneer.moneymaster.data.model.TransactionCategory
import com.pixelpioneer.moneymaster.data.repository.BudgetRepository
import com.pixelpioneer.moneymaster.data.repository.CategoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for managing budget-related data and operations.
 *
 * Handles loading, creating, updating, and deleting budgets,
 * as well as managing categories and form state for budget creation/editing.
 * Provides UI state flows for budgets and categories.
 *
 * @property budgetRepository Repository for budget data access.
 * @property categoryRepository Repository for category data access.
 * @property context Application context for accessing resources.
 */
@HiltViewModel
class BudgetViewModel @Inject constructor(
    private val budgetRepository: BudgetRepository,
    private val categoryRepository: CategoryRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    val budgetsState: StateFlow<UiState<List<Budget>>> =
        budgetRepository.allBudgetsWithSpending
            .map { budgets ->
                if (budgets.isEmpty()) UiState.Empty
                else UiState.Success(budgets)
            }
            .catch { e ->
                emit(UiState.Error(e.message ?: context.getString(R.string.error_loading_budgets)))
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = UiState.Loading
            )

    val categoriesState: StateFlow<UiState<List<TransactionCategory>>> =
        categoryRepository.allCategories
            .map { categories ->
                if (categories.isEmpty()) UiState.Empty
                else UiState.Success(categories)
            }
            .catch { e ->
                emit(UiState.Error(e.message ?: context.getString(R.string.error_unknown)))
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = UiState.Loading
            )

    private val _selectedBudget = MutableStateFlow<UiState<Budget>>(UiState.Loading)
    val selectedBudget: StateFlow<UiState<Budget>> = _selectedBudget

    private val _budgetFormState = MutableStateFlow(BudgetFormState())
    val budgetFormState: StateFlow<BudgetFormState> = _budgetFormState

    fun loadBudgetById(id: Long) {
        viewModelScope.launch {
            budgetRepository.getBudgetById(id)
                .catch { e ->
                    _selectedBudget.value = UiState.Error(
                        e.message ?: context.getString(R.string.error_unknown)
                    )
                }
                .collect { budget ->
                    _selectedBudget.value = UiState.Success(budget)
                }
        }
    }

    fun createBudget() {
        viewModelScope.launch {
            if (!validateBudgetForm()) {
                return@launch
            }

            try {
                val category = _budgetFormState.value.selectedCategory
                    ?: throw IllegalStateException(context.getString(R.string.error_category_null))

                val budget = Budget(
                    category = category,
                    amount = _budgetFormState.value.amount,
                    period = _budgetFormState.value.period,
                    spent = 0.0
                )

                budgetRepository.insertBudget(budget)
                resetFormState()
            } catch (e: Exception) {
                // Error-Handling
            }
        }
    }

    fun updateBudget(id: Long) {
        viewModelScope.launch {
            if (!validateBudgetForm()) {
                return@launch
            }

            try {
                val category = _budgetFormState.value.selectedCategory
                    ?: throw IllegalStateException(context.getString(R.string.error_category_null))

                val budget = Budget(
                    id = id,
                    category = category,
                    amount = _budgetFormState.value.amount,
                    period = _budgetFormState.value.period,
                    spent = 0.0
                )

                budgetRepository.updateBudget(budget)
                resetFormState()
                loadBudgetById(id)
            } catch (e: Exception) {
                // Error-Handling
            }
        }
    }

    fun deleteBudget(budget: Budget) {
        viewModelScope.launch {
            try {
                budgetRepository.deleteBudget(budget)
            } catch (e: Exception) {
                // Error-Handling
            }
        }
    }

    fun updateAmount(amount: Double) {
        _budgetFormState.value = _budgetFormState.value.copy(
            amount = amount,
            amountError = if (amount <= 0) context.getString(R.string.error_amount_greater_zero) else null
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
                amountError = context.getString(R.string.error_amount_greater_zero)
            )
            return false
        }

        if (formState.selectedCategory == null) {
            _budgetFormState.value = formState.copy(
                categoryError = context.getString(R.string.error_select_category)
            )
            return false
        }

        return true
    }

    /**
     * Refreshes budgets (e.g., after an error).
     */
    fun refreshBudgets() {
        viewModelScope.launch {
            try {
                budgetRepository.getBudgetsWithSpendingSync()
            } catch (e: Exception) {
            }
        }
    }
}

/**
 * State holder for the budget form.
 *
 * @property amount The budget amount entered by the user.
 * @property selectedCategory The selected category for the budget.
 * @property period The selected period for the budget.
 * @property amountError Error message for the amount field, if any.
 * @property categoryError Error message for the category field, if any.
 */
data class BudgetFormState(
    val amount: Double = 0.0,
    val selectedCategory: TransactionCategory? = null,
    val period: BudgetPeriod = BudgetPeriod.MONTHLY,
    val amountError: String? = null,
    val categoryError: String? = null
)
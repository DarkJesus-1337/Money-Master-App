package com.pixelpioneer.moneymaster.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pixelpioneer.moneymaster.R
import com.pixelpioneer.moneymaster.data.model.SettingsState
import com.pixelpioneer.moneymaster.data.model.Transaction
import com.pixelpioneer.moneymaster.data.model.TransactionCategory
import com.pixelpioneer.moneymaster.data.repository.CategoryRepository
import com.pixelpioneer.moneymaster.data.repository.SettingsRepository
import com.pixelpioneer.moneymaster.data.repository.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

/**
 * ViewModel for managing user settings and preferences.
 *
 * Handles loading, updating, and persisting user settings such as name, income,
 * recurring costs, and appearance preferences. Provides a state flow for the current settings.
 *
 * @property settingsRepository Repository for accessing and saving settings.
 * @property transactionRepository Repository for managing transactions.
 * @property categoryRepository Repository for managing categories.
 * @property context Application context for accessing resources.
 */
@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val transactionRepository: TransactionRepository,
    private val categoryRepository: CategoryRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {
    private val _state = MutableStateFlow(loadSettings())
    val state: StateFlow<SettingsState> = _state

    private val _fixedCostsOperationState = MutableStateFlow<FixedCostsOperationState>(FixedCostsOperationState.Idle)
    val fixedCostsOperationState: StateFlow<FixedCostsOperationState> = _fixedCostsOperationState

    /**
     * Represents the state of fixed costs operations
     */
    sealed class FixedCostsOperationState {
        object Idle : FixedCostsOperationState()
        object Loading : FixedCostsOperationState()
        data class Success(val message: String) : FixedCostsOperationState()
        data class Error(val message: String) : FixedCostsOperationState()
    }

    /**
     * Loads settings from the repository into a SettingsState object.
     *
     * @return SettingsState containing all user settings
     */
    private fun loadSettings(): SettingsState = SettingsState(
        name = settingsRepository.getName(),
        income = settingsRepository.getIncome(),
        rent = settingsRepository.getRent(),
        electricity = settingsRepository.getElectricity(),
        gas = settingsRepository.getGas(),
        internet = settingsRepository.getInternet(),
        darkMode = settingsRepository.isDarkMode(),
        additionalCosts = settingsRepository.getAdditionalCosts()
    )

    /**
     * Updates the user's name setting.
     *
     * @param name The new name value
     */
    fun updateName(name: String) {
        _state.value = _state.value.copy(name = name)
        settingsRepository.saveName(name)
    }

    /**
     * Updates the user's income setting.
     *
     * @param income The new income value
     */
    fun updateIncome(income: String) {
        _state.value = _state.value.copy(income = income)
        settingsRepository.saveIncome(income)
    }

    /**
     * Updates the user's rent setting.
     *
     * @param rent The new rent value
     */
    fun updateRent(rent: String) {
        _state.value = _state.value.copy(rent = rent)
        settingsRepository.saveRent(rent)
    }

    /**
     * Updates the user's electricity cost setting.
     *
     * @param electricity The new electricity cost value
     */
    fun updateElectricity(electricity: String) {
        _state.value = _state.value.copy(electricity = electricity)
        settingsRepository.saveElectricity(electricity)
    }

    /**
     * Updates the user's gas cost setting.
     *
     * @param gas The new gas cost value
     */
    fun updateGas(gas: String) {
        _state.value = _state.value.copy(gas = gas)
        settingsRepository.saveGas(gas)
    }

    /**
     * Updates the user's internet cost setting.
     *
     * @param internet The new internet cost value
     */
    fun updateInternet(internet: String) {
        _state.value = _state.value.copy(internet = internet)
        settingsRepository.saveInternet(internet)
    }

    /**
     * Updates the dark mode preference.
     *
     * @param dark Whether dark mode should be enabled
     */
    fun updateDarkMode(dark: Boolean) {
        _state.value = _state.value.copy(darkMode = dark)
        settingsRepository.saveDarkMode(dark)
    }

    /**
     * Adds a new additional cost entry.
     */
    fun addAdditionalCost() {
        val updated =
            _state.value.additionalCosts + com.pixelpioneer.moneymaster.data.model.AdditionalCost()
        _state.value = _state.value.copy(additionalCosts = updated)
        settingsRepository.saveAdditionalCosts(updated)
    }

    /**
     * Updates an additional cost at the specified index.
     *
     * @param index The index of the additional cost to update
     * @param label The new label for the cost, or null to keep current value
     * @param value The new cost value, or null to keep current value
     */
    fun updateAdditionalCost(index: Int, label: String? = null, value: String? = null) {
        val costs = _state.value.additionalCosts.toMutableList()
        val current = costs.getOrNull(index) ?: return
        costs[index] = current.copy(
            label = label ?: current.label,
            value = value ?: current.value
        )
        _state.value = _state.value.copy(additionalCosts = costs)
        settingsRepository.saveAdditionalCosts(costs)
    }

    /**
     * Removes an additional cost at the specified index.
     *
     * @param index The index of the additional cost to remove
     */
    fun removeAdditionalCost(index: Int) {
        val costs = _state.value.additionalCosts.toMutableList()
        if (index in costs.indices) {
            costs.removeAt(index)
            _state.value = _state.value.copy(additionalCosts = costs)
            settingsRepository.saveAdditionalCosts(costs)
        }
    }

    /**
     * Creates monthly fixed cost transactions from the saved settings.
     * This function will add all fixed costs as expense transactions for the current month.
     */
    fun createMonthlyFixedCostTransactions() {
        viewModelScope.launch {
            try {
                _fixedCostsOperationState.value = FixedCostsOperationState.Loading

                if (checkIfFixedCostsAlreadyAdded()) {
                    _fixedCostsOperationState.value = FixedCostsOperationState.Error(
                        context.getString(R.string.fixed_costs_already_added)
                    )
                    return@launch
                }

                val housingCategory = getOrCreateHousingCategory()

                val fixedCosts = collectAllFixedCosts()

                if (fixedCosts.isEmpty()) {
                    _fixedCostsOperationState.value = FixedCostsOperationState.Error(
                        context.getString(R.string.no_fixed_costs_defined)
                    )
                    return@launch
                }

                var successCount = 0
                val calendar = Calendar.getInstance()
                calendar.set(Calendar.DAY_OF_MONTH, 1)
                calendar.set(Calendar.HOUR_OF_DAY, 12)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
                val firstDayOfMonth = calendar.timeInMillis

                fixedCosts.forEach { (title, amount) ->
                    val transaction = Transaction(
                        amount = amount,
                        title = title,
                        description = context.getString(R.string.monthly_fixed_cost_description),
                        category = housingCategory,
                        date = firstDayOfMonth,
                        isExpense = true
                    )
                    transactionRepository.insertTransaction(transaction)
                    successCount++
                }

                saveFixedCostsAddedMarker()

                _fixedCostsOperationState.value = FixedCostsOperationState.Success(
                    context.getString(R.string.fixed_costs_added_success, successCount)
                )

            } catch (e: Exception) {
                _fixedCostsOperationState.value = FixedCostsOperationState.Error(
                    e.message ?: context.getString(R.string.error_unknown)
                )
            }
        }
    }

    /**
     * Collects all fixed costs from the settings.
     *
     * @return List of pairs containing title and amount for each fixed cost
     */
    fun collectAllFixedCosts(): List<Pair<String, Double>> {
        val fixedCosts = mutableListOf<Pair<String, Double>>()

        _state.value.rent.toDoubleOrNull()?.let { amount ->
            if (amount > 0) {
                fixedCosts.add(context.getString(R.string.settings_rent).removeSuffix(" (€)") to amount)
            }
        }

        _state.value.electricity.toDoubleOrNull()?.let { amount ->
            if (amount > 0) {
                fixedCosts.add(context.getString(R.string.settings_electricity).removeSuffix(" (€)") to amount)
            }
        }

        _state.value.gas.toDoubleOrNull()?.let { amount ->
            if (amount > 0) {
                fixedCosts.add(context.getString(R.string.settings_gas).removeSuffix(" (€)") to amount)
            }
        }

        _state.value.internet.toDoubleOrNull()?.let { amount ->
            if (amount > 0) {
                fixedCosts.add(context.getString(R.string.settings_internet).removeSuffix(" (€)") to amount)
            }
        }

        _state.value.additionalCosts.forEach { cost ->
            cost.value.toDoubleOrNull()?.let { amount ->
                if (amount > 0 && cost.label.isNotBlank()) {
                    fixedCosts.add(cost.label to amount)
                }
            }
        }

        return fixedCosts
    }

    /**
     * Gets the Housing category or creates it if it doesn't exist.
     *
     * @return The Housing category
     */
    private suspend fun getOrCreateHousingCategory(): TransactionCategory {
        val categories = categoryRepository.allCategories.first()

        val housingCategory = categories.find { category ->
            category.name == context.getString(R.string.category_housing)
        }

        if (housingCategory != null) {
            return housingCategory
        }

        val newCategory = TransactionCategory(
            name = context.getString(R.string.category_housing),
            color = 0xFF2196F3.toInt(),
            icon = 0
        )

        val categoryId = categoryRepository.insertCategory(newCategory)
        return newCategory.copy(id = categoryId)
    }

    /**
     * Checks if fixed costs have already been added for the current month.
     *
     * @return True if fixed costs have been added, false otherwise
     */
    private fun checkIfFixedCostsAlreadyAdded(): Boolean {
        val calendar = Calendar.getInstance()
        val currentMonth = calendar.get(Calendar.MONTH)
        val currentYear = calendar.get(Calendar.YEAR)

        val savedMonth = settingsRepository.getLastFixedCostsMonth()
        val savedYear = settingsRepository.getLastFixedCostsYear()

        return savedMonth == currentMonth && savedYear == currentYear
    }

    /**
     * Saves a marker indicating that fixed costs have been added for the current month.
     */
    private fun saveFixedCostsAddedMarker() {
        val calendar = Calendar.getInstance()
        val currentMonth = calendar.get(Calendar.MONTH)
        val currentYear = calendar.get(Calendar.YEAR)

        settingsRepository.saveLastFixedCostsMonth(currentMonth)
        settingsRepository.saveLastFixedCostsYear(currentYear)
    }

    /**
     * Resets the fixed costs operation state to idle.
     */
    fun resetFixedCostsOperationState() {
        _fixedCostsOperationState.value = FixedCostsOperationState.Idle
    }

    /**
     * Calculates the total amount of all fixed costs.
     *
     * @return The total fixed costs amount
     */
    fun calculateTotalFixedCosts(): Double {
        val fixedCosts = collectAllFixedCosts()
        return fixedCosts.sumOf { it.second }
    }
}
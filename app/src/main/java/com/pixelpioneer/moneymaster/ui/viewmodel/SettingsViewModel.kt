package com.pixelpioneer.moneymaster.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.pixelpioneer.moneymaster.data.model.SettingsState
import com.pixelpioneer.moneymaster.data.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

/**
 * ViewModel for managing user settings and preferences.
 *
 * Handles loading, updating, and persisting user settings such as name, income,
 * recurring costs, and appearance preferences. Provides a state flow for the current settings.
 *
 * @property settingsRepository Repository for accessing and saving settings.
 */
@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {
    private val _state = MutableStateFlow(loadSettings())

    val state: StateFlow<SettingsState> = _state

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
}
package com.pixelpioneer.moneymaster.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.pixelpioneer.moneymaster.data.model.SettingsState
import com.pixelpioneer.moneymaster.data.repository.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SettingsViewModel(private val settingsRepository: SettingsRepository) : ViewModel() {
    private val _state = MutableStateFlow(loadSettings())
    val state: StateFlow<SettingsState> = _state

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

    fun updateName(name: String) {
        _state.value = _state.value.copy(name = name)
        settingsRepository.saveName(name)
    }

    fun updateIncome(income: String) {
        _state.value = _state.value.copy(income = income)
        settingsRepository.saveIncome(income)
    }

    fun updateRent(rent: String) {
        _state.value = _state.value.copy(rent = rent)
        settingsRepository.saveRent(rent)
    }

    fun updateElectricity(electricity: String) {
        _state.value = _state.value.copy(electricity = electricity)
        settingsRepository.saveElectricity(electricity)
    }

    fun updateGas(gas: String) {
        _state.value = _state.value.copy(gas = gas)
        settingsRepository.saveGas(gas)
    }

    fun updateInternet(internet: String) {
        _state.value = _state.value.copy(internet = internet)
        settingsRepository.saveInternet(internet)
    }

    fun updateDarkMode(dark: Boolean) {
        _state.value = _state.value.copy(darkMode = dark)
        settingsRepository.saveDarkMode(dark)
    }

    fun addAdditionalCost() {
        val updated =
            _state.value.additionalCosts + com.pixelpioneer.moneymaster.data.model.AdditionalCost()
        _state.value = _state.value.copy(additionalCosts = updated)
        settingsRepository.saveAdditionalCosts(updated)
    }

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

    fun removeAdditionalCost(index: Int) {
        val costs = _state.value.additionalCosts.toMutableList()
        if (index in costs.indices) {
            costs.removeAt(index)
            _state.value = _state.value.copy(additionalCosts = costs)
            settingsRepository.saveAdditionalCosts(costs)
        }
    }
}
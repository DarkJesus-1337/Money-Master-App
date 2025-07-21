package com.pixelpioneer.moneymaster.data.model

data class AdditionalCost(
    val label: String = "",
    val value: String = ""
)

data class SettingsState(
    val name: String = "",
    val income: String = "",
    val rent: String = "",
    val electricity: String = "",
    val gas: String = "",
    val internet: String = "",
    val darkMode: Boolean = false,
    val additionalCosts: List<AdditionalCost> = emptyList()
) {
    val appVersion: String = "1.7.3"
}

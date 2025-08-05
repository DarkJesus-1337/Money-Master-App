package com.pixelpioneer.moneymaster.data.model

/**
 * Represents an additional cost item in user settings.
 *
 * @property label The name or description of the additional cost
 * @property value The monetary value of the additional cost as a string
 */
data class AdditionalCost(
    val label: String = "",
    val value: String = ""
)

/**
 * Represents the complete settings state of the application.
 *
 * This class stores all user settings including personal information,
 * income, regular expenses, and appearance preferences.
 *
 * @property name User's name
 * @property income User's income as a string
 * @property rent Monthly rent payment as a string
 * @property electricity Monthly electricity cost as a string
 * @property gas Monthly gas cost as a string
 * @property internet Monthly internet cost as a string
 * @property darkMode Whether dark mode is enabled
 * @property additionalCosts List of additional recurring costs defined by the user
 * @property appVersion Current version of the application
 */
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

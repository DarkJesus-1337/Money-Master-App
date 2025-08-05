package com.pixelpioneer.moneymaster.data.repository

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.pixelpioneer.moneymaster.data.model.AdditionalCost
import org.json.JSONArray
import org.json.JSONObject

/**
 * Repository for managing application settings and user preferences.
 *
 * Handles storing and retrieving user settings using SharedPreferences,
 * including personal details, financial information, and app appearance preferences.
 *
 * @property context The application context for SharedPreferences access
 */
class SettingsRepository(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("settings", Context.MODE_PRIVATE)

    /**
     * Saves the user's name.
     *
     * @param name The user's name to save
     */
    fun saveName(name: String) = prefs.edit { putString("name", name) }

    /**
     * Retrieves the user's name.
     *
     * @return The user's name, or empty string if not set
     */
    fun getName(): String = prefs.getString("name", "") ?: ""

    /**
     * Saves the user's income.
     *
     * @param income The user's income to save
     */
    fun saveIncome(income: String) = prefs.edit { putString("income", income) }

    /**
     * Retrieves the user's income.
     *
     * @return The user's income, or empty string if not set
     */
    fun getIncome(): String = prefs.getString("income", "") ?: ""

    /**
     * Saves the dark mode setting.
     *
     * @param enabled Whether dark mode should be enabled
     */
    fun saveDarkMode(enabled: Boolean) = prefs.edit { putBoolean("darkMode", enabled) }

    /**
     * Checks if dark mode is enabled.
     *
     * @return True if dark mode is enabled, false otherwise
     */
    fun isDarkMode(): Boolean = prefs.getBoolean("darkMode", false)

    /**
     * Saves the user's rent expense.
     *
     * @param rent The rent amount to save
     */
    fun saveRent(rent: String) = prefs.edit { putString("rent", rent) }

    /**
     * Retrieves the user's rent expense.
     *
     * @return The rent amount, or empty string if not set
     */
    fun getRent(): String = prefs.getString("rent", "") ?: ""

    /**
     * Saves the user's electricity expense.
     *
     * @param electricity The electricity amount to save
     */
    fun saveElectricity(electricity: String) = prefs.edit { putString("electricity", electricity) }

    /**
     * Retrieves the user's electricity expense.
     *
     * @return The electricity amount, or empty string if not set
     */
    fun getElectricity(): String = prefs.getString("electricity", "") ?: ""

    /**
     * Saves the user's gas expense.
     *
     * @param gas The gas amount to save
     */
    fun saveGas(gas: String) = prefs.edit { putString("gas", gas) }

    /**
     * Retrieves the user's gas expense.
     *
     * @return The gas amount, or empty string if not set
     */
    fun getGas(): String = prefs.getString("gas", "") ?: ""

    /**
     * Saves the user's internet expense.
     *
     * @param internet The internet amount to save
     */
    fun saveInternet(internet: String) = prefs.edit { putString("internet", internet) }

    /**
     * Retrieves the user's internet expense.
     *
     * @return The internet amount, or empty string if not set
     */
    fun getInternet(): String = prefs.getString("internet", "") ?: ""

    /**
     * Saves the list of additional costs defined by the user.
     *
     * Converts the list to JSON format before storing in SharedPreferences.
     *
     * @param costs The list of additional costs to save
     */
    fun saveAdditionalCosts(costs: List<AdditionalCost>) {
        val jsonArray = JSONArray()
        costs.forEach {
            val obj = JSONObject()
            obj.put("label", it.label)
            obj.put("value", it.value)
            jsonArray.put(obj)
        }
        prefs.edit().putString("additionalCosts", jsonArray.toString()).apply()
    }

    /**
     * Retrieves the list of additional costs defined by the user.
     *
     * Parses the stored JSON data back into a list of AdditionalCost objects.
     *
     * @return The list of additional costs, or empty list if none are set or an error occurs
     */
    fun getAdditionalCosts(): List<AdditionalCost> {
        val json = prefs.getString("additionalCosts", null) ?: return emptyList()
        val result = mutableListOf<AdditionalCost>()
        try {
            val array = JSONArray(json)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                result.add(
                    AdditionalCost(
                        label = obj.optString("label", ""),
                        value = obj.optString("value", "")
                    )
                )
            }
        } catch (_: Exception) {
        }
        return result
    }

}
// Datei: SettingsRepository.kt
package com.pixelpioneer.moneymaster.data.repository

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.pixelpioneer.moneymaster.data.model.AdditionalCost
import org.json.JSONArray
import org.json.JSONObject

class SettingsRepository(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("settings", Context.MODE_PRIVATE)

    fun saveName(name: String) = prefs.edit { putString("name", name) }
    fun getName(): String = prefs.getString("name", "") ?: ""

    fun saveIncome(income: String) = prefs.edit { putString("income", income) }
    fun getIncome(): String = prefs.getString("income", "") ?: ""

    fun saveDarkMode(enabled: Boolean) = prefs.edit { putBoolean("darkMode", enabled) }
    fun isDarkMode(): Boolean = prefs.getBoolean("darkMode", false)

    fun saveRent(rent: String) = prefs.edit { putString("rent", rent) }
    fun getRent(): String = prefs.getString("rent", "") ?: ""

    fun saveElectricity(electricity: String) = prefs.edit { putString("electricity", electricity) }
    fun getElectricity(): String = prefs.getString("electricity", "") ?: ""

    fun saveGas(gas: String) = prefs.edit { putString("gas", gas) }
    fun getGas(): String = prefs.getString("gas", "") ?: ""

    fun saveInternet(internet: String) = prefs.edit { putString("internet", internet) }
    fun getInternet(): String = prefs.getString("internet", "") ?: ""

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
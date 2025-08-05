package com.pixelpioneer.moneymaster.data.provider

import android.content.Context
import com.pixelpioneer.moneymaster.R
import com.pixelpioneer.moneymaster.data.model.TransactionCategory

/**
 * Provider for predefined transaction categories.
 * This class provides a list of standard categories that are
 * immediately available in the app without database loading.
 */
object PredefinedCategoriesProvider {

    /**
     * Returns a list of predefined categories.
     * These categories are always available and don't need to be loaded from the database.
     *
     * @param context The application context to access string resources
     * @return List of predefined TransactionCategory objects
     */
    fun getPredefinedCategories(context: Context): List<TransactionCategory> {
        return listOf(
            TransactionCategory(
                id = 1,
                name = context.getString(R.string.category_groceries),
                color = 0xFF4CAF50.toInt(),
                icon = 0
            ),
            TransactionCategory(
                id = 2,
                name = context.getString(R.string.category_housing),
                color = 0xFF2196F3.toInt(),
                icon = 0
            ),
            TransactionCategory(
                id = 3,
                name = context.getString(R.string.category_transport),
                color = 0xFF9C27B0.toInt(),
                icon = 0
            ),
            TransactionCategory(
                id = 4,
                name = context.getString(R.string.category_entertainment),
                color = 0xFFFF9800.toInt(),
                icon = 0
            ),
            TransactionCategory(
                id = 5,
                name = context.getString(R.string.category_health),
                color = 0xFFF44336.toInt(),
                icon = 0
            ),
            TransactionCategory(
                id = 6,
                name = context.getString(R.string.category_shopping),
                color = 0xFFE91E63.toInt(),
                icon = 0
            ),
            TransactionCategory(
                id = 7,
                name = context.getString(R.string.category_education),
                color = 0xFF3F51B5.toInt(),
                icon = 0
            ),
            TransactionCategory(
                id = 8,
                name = context.getString(R.string.category_salary),
                color = 0xFF00BCD4.toInt(),
                icon = 0
            ),
            TransactionCategory(
                id = 9,
                name = context.getString(R.string.category_gifts),
                color = 0xFF8BC34A.toInt(),
                icon = 0
            ),
            TransactionCategory(
                id = 10,
                name = context.getString(R.string.category_other),
                color = 0xFF607D8B.toInt(),
                icon = 0
            )
        )
    }
}
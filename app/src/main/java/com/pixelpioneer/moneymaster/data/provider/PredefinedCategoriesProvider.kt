package com.pixelpioneer.moneymaster.data.provider

import android.content.Context
import com.pixelpioneer.moneymaster.R
import com.pixelpioneer.moneymaster.data.model.TransactionCategory

/**
 * Provider für vordefinierte Kategorien.
 * Diese Klasse stellt eine Liste von Standard-Kategorien bereit,
 * die sofort in der App verfügbar sind.
 */
object PredefinedCategoriesProvider {
    
    /**
     * Gibt eine Liste vordefinierter Kategorien zurück.
     * Diese Kategorien sind immer verfügbar und müssen nicht von der Datenbank geladen werden.
     */
    fun getPredefinedCategories(context: Context): List<TransactionCategory> {
        return listOf(
            TransactionCategory(
                id = 1,
                name = context.getString(R.string.category_groceries),
                color = 0xFF4CAF50.toInt(), // Grün für Lebensmittel
                icon = 0
            ),
            TransactionCategory(
                id = 2,
                name = context.getString(R.string.category_housing),
                color = 0xFF2196F3.toInt(), // Blau für Wohnen
                icon = 0
            ),
            TransactionCategory(
                id = 3,
                name = context.getString(R.string.category_transport),
                color = 0xFF9C27B0.toInt(), // Lila für Transport
                icon = 0
            ),
            TransactionCategory(
                id = 4,
                name = context.getString(R.string.category_entertainment),
                color = 0xFFFF9800.toInt(), // Orange für Unterhaltung
                icon = 0
            ),
            TransactionCategory(
                id = 5,
                name = context.getString(R.string.category_health),
                color = 0xFFF44336.toInt(), // Rot für Gesundheit
                icon = 0
            ),
            TransactionCategory(
                id = 6,
                name = context.getString(R.string.category_shopping),
                color = 0xFFE91E63.toInt(), // Pink für Einkaufen
                icon = 0
            ),
            TransactionCategory(
                id = 7,
                name = context.getString(R.string.category_education),
                color = 0xFF3F51B5.toInt(), // Indigo für Bildung
                icon = 0
            ),
            TransactionCategory(
                id = 8,
                name = context.getString(R.string.category_salary),
                color = 0xFF00BCD4.toInt(), // Cyan für Gehalt
                icon = 0
            ),
            TransactionCategory(
                id = 9,
                name = context.getString(R.string.category_gifts),
                color = 0xFF8BC34A.toInt(), // Hellgrün für Geschenke
                icon = 0
            ),
            TransactionCategory(
                id = 10,
                name = context.getString(R.string.category_other),
                color = 0xFF607D8B.toInt(), // Blaugrau für Sonstiges
                icon = 0
            )
        )
    }
}
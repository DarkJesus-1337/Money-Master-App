package com.pixelpioneer.moneymaster.data.migration

import android.content.Context
import com.pixelpioneer.moneymaster.data.local.db.dao.CategoryDao
import com.pixelpioneer.moneymaster.data.local.db.dao.TransactionDao
import com.pixelpioneer.moneymaster.data.local.entity.CategoryEntity
import com.pixelpioneer.moneymaster.R
import kotlinx.coroutines.flow.first

/**
 * Helper-Klasse für Datenbank-Migrationen und Datenreparaturen.
 * Behebt Probleme mit fehlenden Kategorie-Referenzen.
 */
class DatabaseMigrationHelper(
    private val categoryDao: CategoryDao,
    private val transactionDao: TransactionDao,
    private val context: Context
) {

    /**
     * Initialisiert die Datenbank mit Standard-Kategorien und repariert
     * alle bestehenden Transaktionen mit ungültigen Kategorie-Referenzen.
     */
    suspend fun initializeAndRepairDatabase() {
        // 1. Prüfen ob Kategorien existieren
        val existingCategories = categoryDao.getAllCategories().first()
        
        if (existingCategories.isEmpty()) {
            // 2. Standard-Kategorien einfügen
            val defaultCategories = getDefaultCategories()
            categoryDao.insertAll(defaultCategories)
        }
        
        // 3. Alle Kategorien nach der Initialisierung laden
        val allCategories = categoryDao.getAllCategories().first()
        val defaultCategoryId = allCategories.firstOrNull()?.id ?: 1
        
        // 4. Alle Transaktionen mit ungültigen categoryId reparieren
        val orphanedTransactions = transactionDao.getTransactionsWithInvalidCategories()
        
        if (orphanedTransactions.isNotEmpty()) {
            // Alle "verwaisten" Transaktionen zur Standard-Kategorie zuweisen
            transactionDao.updateCategoryIdForOrphanedTransactions(defaultCategoryId)
        }
    }

    /**
     * Erstellt die Standard-Kategorien für die Datenbank.
     */
    private fun getDefaultCategories(): List<CategoryEntity> {
        return listOf(
            CategoryEntity(
                name = context.getString(R.string.category_groceries),
                color = 0xFF4CAF50.toInt(),
                iconResId = 0
            ),
            CategoryEntity(
                name = context.getString(R.string.category_housing),
                color = 0xFF2196F3.toInt(),
                iconResId = 0
            ),
            CategoryEntity(
                name = context.getString(R.string.category_transport),
                color = 0xFF9C27B0.toInt(),
                iconResId = 0
            ),
            CategoryEntity(
                name = context.getString(R.string.category_entertainment),
                color = 0xFFFF9800.toInt(),
                iconResId = 0
            ),
            CategoryEntity(
                name = context.getString(R.string.category_health),
                color = 0xFFF44336.toInt(),
                iconResId = 0
            ),
            CategoryEntity(
                name = context.getString(R.string.category_shopping),
                color = 0xFFE91E63.toInt(),
                iconResId = 0
            ),
            CategoryEntity(
                name = context.getString(R.string.category_education),
                color = 0xFF3F51B5.toInt(),
                iconResId = 0
            ),
            CategoryEntity(
                name = context.getString(R.string.category_salary),
                color = 0xFF00BCD4.toInt(),
                iconResId = 0
            ),
            CategoryEntity(
                name = context.getString(R.string.category_gifts),
                color = 0xFF8BC34A.toInt(),
                iconResId = 0
            ),
            CategoryEntity(
                name = context.getString(R.string.category_other),
                color = 0xFF607D8B.toInt(),
                iconResId = 0
            )
        )
    }
}
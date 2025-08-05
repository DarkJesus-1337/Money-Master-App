package com.pixelpioneer.moneymaster.data.repository

import android.content.Context
import com.pixelpioneer.moneymaster.R
import com.pixelpioneer.moneymaster.data.local.db.dao.CategoryDao
import com.pixelpioneer.moneymaster.data.local.db.dao.TransactionDao
import com.pixelpioneer.moneymaster.data.local.entity.CategoryEntity
import com.pixelpioneer.moneymaster.data.mapper.CategoryMapper
import com.pixelpioneer.moneymaster.data.model.TransactionCategory
import com.pixelpioneer.moneymaster.data.provider.PredefinedCategoriesProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.first

/**
 * Repository for managing transaction categories.
 * Kombiniert Datenbank-Kategorien mit vordefinierten Kategorien und
 * repariert automatisch Datenbank-Inkonsistenzen.
 */
class CategoryRepository(
    private val categoryDao: CategoryDao,
    private val transactionDao: TransactionDao,
    private val context: Context
) {

    /**
     * Flow aller verfügbaren Kategorien.
     * Wenn die Datenbank leer ist, werden vordefinierte Kategorien zurückgegeben.
     */
    val allCategories: Flow<List<TransactionCategory>> = categoryDao.getAllCategories()
        .map { dbCategories ->
            if (dbCategories.isEmpty()) {
                // Wenn keine Kategorien in der DB sind, vordefinierte zurückgeben
                PredefinedCategoriesProvider.getPredefinedCategories(context)
            } else {
                // Ansonsten DB-Kategorien verwenden
                dbCategories.map { CategoryMapper.fromEntity(it) }
            }
        }

    /**
     * Initialisiert die Datenbank mit vordefinierten Kategorien und
     * repariert alle bestehenden Transaktionen mit ungültigen Kategorie-Referenzen.
     * Diese Methode sollte beim ersten App-Start aufgerufen werden.
     */
    suspend fun initializeDefaultCategoriesAndRepairDatabase() {
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

        // 4. Prüfen ob Transaktionen mit ungültigen categoryId existieren
        val orphanedCount = transactionDao.countTransactionsWithInvalidCategories()

        if (orphanedCount > 0) {
            // Alle "verwaisten" Transaktionen zur ersten verfügbaren Kategorie zuweisen
            transactionDao.updateCategoryIdForOrphanedTransactions(defaultCategoryId)
        }
    }

    /**
     * Gibt vordefinierte Kategorien als Fallback zurück.
     * Diese werden verwendet, wenn die Datenbank leer ist.
     */
    fun getPredefinedCategories(): List<TransactionCategory> {
        return PredefinedCategoriesProvider.getPredefinedCategories(context)
    }

    /**
     * Retrieves a specific category by its ID.
     */
    fun getCategoryById(id: Long): Flow<TransactionCategory> {
        return categoryDao.getCategoryById(id)
            .map { CategoryMapper.fromEntity(it) }
    }

    /**
     * Inserts a new category into the database.
     */
    suspend fun insertCategory(category: TransactionCategory): Long {
        val entity = CategoryMapper.toEntity(category)
        return categoryDao.insertCategory(entity)
    }

    /**
     * Updates an existing category in the database.
     */
    suspend fun updateCategory(category: TransactionCategory) {
        val entity = CategoryMapper.toEntity(category)
        categoryDao.updateCategory(entity)
    }

    /**
     * Deletes a category from the database.
     */
    suspend fun deleteCategory(category: TransactionCategory) {
        val entity = CategoryMapper.toEntity(category)
        categoryDao.deleteCategory(entity)
    }

    /**
     * Erstellt die Standard CategoryEntity-Liste für die Datenbank.
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
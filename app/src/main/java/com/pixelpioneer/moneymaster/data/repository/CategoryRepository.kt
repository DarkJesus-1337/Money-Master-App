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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

/**
 * Repository for managing transaction categories.
 *
 * Combines database categories with predefined categories and
 * automatically repairs database inconsistencies.
 *
 * @property categoryDao Data access object for category entities
 * @property transactionDao Data access object for transaction entities
 * @property context The application context for resources access
 */
class CategoryRepository(
    private val categoryDao: CategoryDao,
    private val transactionDao: TransactionDao,
    private val context: Context
) {

    /**
     * Flow of all available categories.
     *
     * If the database is empty, returns predefined categories.
     */
    val allCategories: Flow<List<TransactionCategory>> = categoryDao.getAllCategories()
        .map { dbCategories ->
            if (dbCategories.isEmpty()) {
                PredefinedCategoriesProvider.getPredefinedCategories(context)
            } else {
                dbCategories.map { CategoryMapper.fromEntity(it) }
            }
        }

    /**
     * Initializes the database with predefined categories and
     * repairs all existing transactions with invalid category references.
     *
     * This method should be called on first app startup.
     */
    suspend fun initializeDefaultCategoriesAndRepairDatabase() {
        val existingCategories = categoryDao.getAllCategories().first()

        if (existingCategories.isEmpty()) {
            val defaultCategories = getDefaultCategories()
            categoryDao.insertAll(defaultCategories)
        }

        val allCategories = categoryDao.getAllCategories().first()
        val defaultCategoryId = allCategories.firstOrNull()?.id ?: 1

        val orphanedCount = transactionDao.countTransactionsWithInvalidCategories()

        if (orphanedCount > 0) {
            transactionDao.updateCategoryIdForOrphanedTransactions(defaultCategoryId)
        }
    }

    /**
     * Returns predefined categories as a fallback.
     * These are used when the database is empty.
     *
     * @return List of predefined transaction categories
     */
    fun getPredefinedCategories(): List<TransactionCategory> {
        return PredefinedCategoriesProvider.getPredefinedCategories(context)
    }

    /**
     * Inserts a new category into the database.
     *
     * @param category The category to insert
     * @return The ID of the newly inserted category
     */
    suspend fun insertCategory(category: TransactionCategory): Long {
        val entity = CategoryMapper.toEntity(category)
        return categoryDao.insertCategory(entity)
    }

    /**
     * Updates an existing category in the database.
     *
     * @param category The category with updated values
     */
    suspend fun updateCategory(category: TransactionCategory) {
        val entity = CategoryMapper.toEntity(category)
        categoryDao.updateCategory(entity)
    }

    /**
     * Deletes a category from the database.
     *
     * @param category The category to delete
     */
    suspend fun deleteCategory(category: TransactionCategory) {
        val entity = CategoryMapper.toEntity(category)
        categoryDao.deleteCategory(entity)
    }

    /**
     * Creates the default CategoryEntity list for the database.
     *
     * @return List of default category entities
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
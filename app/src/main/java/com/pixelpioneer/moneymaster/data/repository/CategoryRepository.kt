package com.pixelpioneer.moneymaster.data.repository

import com.pixelpioneer.moneymaster.data.db.CategoryDao
import com.pixelpioneer.moneymaster.data.entity.CategoryEntity
import com.pixelpioneer.moneymaster.data.mapper.CategoryMapper
import com.pixelpioneer.moneymaster.data.model.TransactionCategory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Repository for managing transaction categories.
 *
 * Handles loading, inserting, updating, and deleting categories,
 * as well as providing default categories for initialization.
 *
 * @property categoryDao Data access object for category entities.
 */
class CategoryRepository(private val categoryDao: CategoryDao) {

    /**
     * Flow of all available transaction categories.
     */
    val allCategories = categoryDao.getAllCategories()
        .map { list -> list.map { CategoryMapper.fromEntity(it) } }

    /**
     * Retrieves a specific category by its ID.
     *
     * @param id The unique identifier of the category.
     * @return A [Flow] containing the [TransactionCategory].
     */
    fun getCategoryById(id: Long): Flow<TransactionCategory> {
        return categoryDao.getCategoryById(id)
            .map { CategoryMapper.fromEntity(it) }
    }

    /**
     * Inserts a new category into the database.
     *
     * @param category The category to insert.
     * @return The ID of the newly inserted category.
     */
    suspend fun insertCategory(category: TransactionCategory): Long {
        val entity = CategoryMapper.toEntity(category)
        return categoryDao.insertCategory(entity)
    }

    /**
     * Updates an existing category in the database.
     *
     * @param category The category with updated information.
     */
    suspend fun updateCategory(category: TransactionCategory) {
        val entity = CategoryMapper.toEntity(category)
        categoryDao.updateCategory(entity)
    }

    /**
     * Deletes a category from the database.
     *
     * @param category The category to delete.
     */
    suspend fun deleteCategory(category: TransactionCategory) {
        val entity = CategoryMapper.toEntity(category)
        categoryDao.deleteCategory(entity)
    }

    /**
     * Inserts default categories into the database.
     *
     * This method is typically called during app initialization
     * to populate the database with standard categories.
     */
    suspend fun insertDefaultCategories() {
        val defaultCategories = getDefaultCategories()
        categoryDao.insertAll(defaultCategories)
    }

    /**
     * Creates a list of default categories for the application.
     *
     * @return A list of default [CategoryEntity] objects with predefined names, colors, and icons.
     */
    private fun getDefaultCategories(): List<CategoryEntity> {
        return listOf(
            CategoryEntity(name = "Lebensmittel", color = 0xFF4CAF50.toInt(), iconResId = 0),
            CategoryEntity(name = "Wohnen", color = 0xFF2196F3.toInt(), iconResId = 0),
            CategoryEntity(name = "Transport", color = 0xFF9C27B0.toInt(), iconResId = 0),
            CategoryEntity(name = "Unterhaltung", color = 0xFFFF9800.toInt(), iconResId = 0),
            CategoryEntity(name = "Gesundheit", color = 0xFFF44336.toInt(), iconResId = 0),
            CategoryEntity(name = "Shopping", color = 0xFFE91E63.toInt(), iconResId = 0),
            CategoryEntity(name = "Bildung", color = 0xFF3F51B5.toInt(), iconResId = 0),
            CategoryEntity(name = "Gehalt", color = 0xFF00BCD4.toInt(), iconResId = 0),
            CategoryEntity(name = "Geschenke", color = 0xFF8BC34A.toInt(), iconResId = 0),
            CategoryEntity(name = "Sonstiges", color = 0xFF607D8B.toInt(), iconResId = 0)
        )
    }
}
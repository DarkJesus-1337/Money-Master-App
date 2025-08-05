package com.pixelpioneer.moneymaster.data.local.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.pixelpioneer.moneymaster.data.local.entity.CategoryEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for the categories table.
 * Provides methods to query, insert, update and delete category data.
 */
@Dao
interface CategoryDao {
    /**
     * Gets all categories from the database.
     *
     * @return Flow emitting a list of all category entities
     */
    @Query("SELECT * FROM categories")
    fun getAllCategories(): Flow<List<CategoryEntity>>

    /**
     * Gets a specific category by its ID.
     *
     * @param id The ID of the category to retrieve
     * @return Flow emitting the category entity
     */
    @Query("SELECT * FROM categories WHERE id = :id")
    fun getCategoryById(id: Long): Flow<CategoryEntity>

    /**
     * Inserts a category into the database.
     * If a category with the same ID already exists, it is replaced.
     *
     * @param category The category entity to insert
     * @return The ID of the inserted category
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: CategoryEntity): Long

    /**
     * Updates an existing category in the database.
     *
     * @param category The category entity to update
     */
    @Update
    suspend fun updateCategory(category: CategoryEntity)

    /**
     * Deletes a category from the database.
     *
     * @param category The category entity to delete
     */
    @Delete
    suspend fun deleteCategory(category: CategoryEntity)

    /**
     * Inserts a list of categories into the database.
     * If a category with the same ID already exists, it is ignored.
     *
     * @param categories The list of category entities to insert
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(categories: List<CategoryEntity>)
}
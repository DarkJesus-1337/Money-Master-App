package com.pixelpioneer.moneymaster.data.local.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.pixelpioneer.moneymaster.data.local.entity.BudgetEntity
import com.pixelpioneer.moneymaster.data.local.relation.BudgetWithCategory
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for the budgets table.
 * Provides methods to query, insert, update and delete budget data.
 */
@Dao
interface BudgetDao {
    /**
     * Gets all budgets from the database as LiveData.
     *
     * @return LiveData list of all budget entities
     */
    @Query("SELECT * FROM budgets")
    fun getAllBudgets(): Flow<List<BudgetEntity>>

    /**
     * Gets a specific budget by its ID.
     *
     * @param id The ID of the budget to retrieve
     * @return Flow emitting the budget entity
     */
    @Query("SELECT * FROM budgets WHERE id = :id")
    fun getBudgetById(id: Long): Flow<BudgetEntity>

    /**
     * Gets all budgets with their associated categories.
     *
     * @return Flow emitting a list of BudgetWithCategory objects
     */
    @Transaction
    @Query("SELECT * FROM budgets")
    fun getBudgetsWithCategory(): Flow<List<BudgetWithCategory>>

    /**
     * Gets a specific budget with its associated category by budget ID.
     *
     * @param id The ID of the budget to retrieve
     * @return Flow emitting a BudgetWithCategory object
     */
    @Transaction
    @Query("SELECT * FROM budgets WHERE id = :id")
    fun getBudgetWithCategoryById(id: Long): Flow<BudgetWithCategory>

    /**
     * Gets all budgets for a specific category.
     *
     * @param categoryId The ID of the category to filter by
     * @return Flow emitting a list of budget entities for the specified category
     */
    @Query("SELECT * FROM budgets WHERE categoryId = :categoryId")
    fun getBudgetsByCategory(categoryId: Long): Flow<List<BudgetEntity>>

    /**
     * Inserts a budget into the database.
     * If a budget with the same ID already exists, it is replaced.
     *
     * @param budget The budget entity to insert
     * @return The ID of the inserted budget
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBudget(budget: BudgetEntity): Long

    /**
     * Updates an existing budget in the database.
     *
     * @param budget The budget entity to update
     */
    @Update
    suspend fun updateBudget(budget: BudgetEntity)

    /**
     * Deletes a budget from the database.
     *
     * @param budget The budget entity to delete
     */
    @Delete
    suspend fun deleteBudget(budget: BudgetEntity)

    /**
     * Gets all budgets with their associated categories synchronously.
     *
     * @return List of BudgetWithCategory objects
     */
    @Transaction
    @Query("SELECT * FROM budgets")
    suspend fun getBudgetsWithCategorySync(): List<BudgetWithCategory>
}
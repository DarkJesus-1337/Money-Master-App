package com.pixelpioneer.moneymaster.data.db

import androidx.room.*
import com.pixelpioneer.moneymaster.data.entity.BudgetEntity
import com.pixelpioneer.moneymaster.data.relation.BudgetWithCategory
import kotlinx.coroutines.flow.Flow

@Dao
interface BudgetDao {
    @Query("SELECT * FROM budgets")
    fun getAllBudgets(): Flow<List<BudgetEntity>>

    @Query("SELECT * FROM budgets WHERE id = :id")
    fun getBudgetById(id: Long): Flow<BudgetEntity>

    @Transaction
    @Query("SELECT * FROM budgets")
    fun getBudgetsWithCategory(): Flow<List<BudgetWithCategory>>

    @Transaction
    @Query("SELECT * FROM budgets WHERE id = :id")
    fun getBudgetWithCategoryById(id: Long): Flow<BudgetWithCategory>

    @Query("SELECT * FROM budgets WHERE categoryId = :categoryId")
    fun getBudgetsByCategory(categoryId: Long): Flow<List<BudgetEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBudget(budget: BudgetEntity): Long

    @Update
    suspend fun updateBudget(budget: BudgetEntity)

    @Delete
    suspend fun deleteBudget(budget: BudgetEntity)

    @Transaction
    @Query("SELECT * FROM budgets")
    suspend fun getBudgetsWithCategorySync(): List<BudgetWithCategory>
}
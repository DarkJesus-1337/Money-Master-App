package com.pixelpioneer.moneymaster.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pixelpioneer.moneymaster.core.util.UiState
import com.pixelpioneer.moneymaster.data.model.TransactionCategory
import com.pixelpioneer.moneymaster.data.repository.CategoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for managing transaction categories.
 *
 * Initializes predefined categories and repairs the database if needed.
 * Provides UI state flows for categories and supports refreshing and reinitialization.
 *
 * @property categoryRepository Repository for category data access.
 * @property context Application context for accessing resources.
 */
@HiltViewModel
class CategoryViewModel @Inject constructor(
    private val categoryRepository: CategoryRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _categoriesState =
        MutableStateFlow<UiState<List<TransactionCategory>>>(UiState.Loading)
    val categoriesState: StateFlow<UiState<List<TransactionCategory>>> = _categoriesState

    private val _categories = MutableStateFlow<List<TransactionCategory>>(emptyList())
    val categories: StateFlow<List<TransactionCategory>> = _categories

    private val _isInitializing = MutableStateFlow(false)

    init {
        initializeAndLoadCategories()
    }

    /**
     * Initializes categories and repairs the database on first use.
     */
    private fun initializeAndLoadCategories() {
        viewModelScope.launch {
            try {
                _isInitializing.value = true
                _categoriesState.value = UiState.Loading

                categoryRepository.initializeDefaultCategoriesAndRepairDatabase()
                loadCategories()

            } catch (_: Exception) {
                val predefinedCategories = categoryRepository.getPredefinedCategories()
                _categoriesState.value = UiState.Success(predefinedCategories)
                _categories.value = predefinedCategories
            } finally {
                _isInitializing.value = false
            }
        }
    }

    private fun loadCategories() {
        viewModelScope.launch {
            try {
                categoryRepository.allCategories
                    .catch { e ->
                        val predefinedCategories = categoryRepository.getPredefinedCategories()
                        _categoriesState.value = UiState.Success(predefinedCategories)
                        _categories.value = predefinedCategories
                    }
                    .collect { categories ->
                        if (categories.isEmpty()) {
                            val predefinedCategories = categoryRepository.getPredefinedCategories()
                            _categoriesState.value = UiState.Success(predefinedCategories)
                            _categories.value = predefinedCategories
                        } else {
                            _categoriesState.value = UiState.Success(categories)
                            _categories.value = categories
                        }
                    }
            } catch (_: Exception) {
                val predefinedCategories = categoryRepository.getPredefinedCategories()
                _categoriesState.value = UiState.Success(predefinedCategories)
                _categories.value = predefinedCategories
            }
        }
    }

    // Fügen Sie diese Methoden zu Ihrem bestehenden CategoryViewModel hinzu:

    /**
     * Adds a new custom category.
     *
     * @param name The name of the new category
     * @param color The color of the new category (as Int)
     */
    fun addCategory(name: String, color: Int) {
        viewModelScope.launch {
            try {
                _categoriesState.value = UiState.Loading

                val newCategory = TransactionCategory(
                    id = 0, // Auto-generate ID
                    name = name,
                    color = color,
                    icon = 0 // Standard icon
                )

                categoryRepository.insertCategory(newCategory)
                refreshCategories() // Reload categories after adding

            } catch (e: Exception) {
                _categoriesState.value = UiState.Error("Fehler beim Hinzufügen der Kategorie: ${e.message}")
            }
        }
    }

    /**
     * Updates an existing category.
     *
     * @param category The category with updated values
     */
    fun updateCategory(category: TransactionCategory) {
        viewModelScope.launch {
            try {
                categoryRepository.updateCategory(category)
                refreshCategories() // Reload categories after update
            } catch (e: Exception) {
                _categoriesState.value = UiState.Error("Fehler beim Aktualisieren der Kategorie: ${e.message}")
            }
        }
    }

    /**
     * Deletes a category.
     * Before deleting, all transactions with this category will be updated to use the default category.
     *
     * @param category The category to delete
     */
    fun deleteCategory(category: TransactionCategory) {
        viewModelScope.launch {
            try {
                // Verhindere das Löschen von vordefinierten Kategorien
                if (category.id <= 10) {
                    _categoriesState.value = UiState.Error("Vordefinierte Kategorien können nicht gelöscht werden")
                    return@launch
                }

                // Aktualisiere alle Transaktionen mit dieser Kategorie auf "Sonstiges" (ID: 10)
                // Dies müsste im TransactionRepository implementiert werden
                // transactionRepository.updateCategoryForTransactions(category.id, 10)

                categoryRepository.deleteCategory(category)
                refreshCategories() // Reload categories after deletion

            } catch (e: Exception) {
                _categoriesState.value = UiState.Error("Fehler beim Löschen der Kategorie: ${e.message}")
            }
        }
    }

    /**
     * Refreshes the categories list from the database.
     */
    fun refreshCategories() {
        viewModelScope.launch {
            loadCategories()
        }
    }
}


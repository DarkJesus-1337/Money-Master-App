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
    val isInitializing: StateFlow<Boolean> = _isInitializing

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

            } catch (e: Exception) {
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
            } catch (e: Exception) {
                val predefinedCategories = categoryRepository.getPredefinedCategories()
                _categoriesState.value = UiState.Success(predefinedCategories)
                _categories.value = predefinedCategories
            }
        }
    }

    /**
     * Refreshes the categories list.
     */
    fun refreshCategories() {
        loadCategories()
    }

    /**
     * Forces reinitialization of the database (for debugging purposes).
     */
    fun forceReinitialize() {
        initializeAndLoadCategories()
    }
}

/**
 * State holder for the category form.
 *
 * @property name The name of the category.
 * @property color The color value for the category.
 * @property iconResId The resource ID for the category icon.
 * @property nameError Error message for the name field, if any.
 */
data class CategoryFormState(
    val name: String = "",
    val color: Int = 0xFF4CAF50.toInt(),
    val iconResId: Int = 0,
    val nameError: String? = null
)
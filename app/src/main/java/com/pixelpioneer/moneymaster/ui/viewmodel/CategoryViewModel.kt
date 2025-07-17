package com.pixelpioneer.moneymaster.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pixelpioneer.moneymaster.R
import com.pixelpioneer.moneymaster.data.model.TransactionCategory
import com.pixelpioneer.moneymaster.data.repository.CategoryRepository
import com.pixelpioneer.moneymaster.util.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

/**
 * ViewModel for managing transaction categories.
 *
 * Handles loading, creating, updating, and deleting categories,
 * as well as managing the category form state and providing UI state flows.
 *
 * @property categoryRepository Repository for category data access.
 */
class CategoryViewModel(
    private val categoryRepository: CategoryRepository,
    private val context: Context
) : ViewModel() {

    private val _categoriesState =
        MutableStateFlow<UiState<List<TransactionCategory>>>(UiState.Loading)

    private val _selectedCategory = MutableStateFlow<UiState<TransactionCategory>>(UiState.Loading)

    private val _categoryFormState = MutableStateFlow(CategoryFormState())

    private val _categories = MutableStateFlow<List<TransactionCategory>>(emptyList())
    val categories: StateFlow<List<TransactionCategory>> = _categories

    init {
        loadCategories()
    }

    private fun loadCategories() {
        viewModelScope.launch {
            try {
                _categoriesState.value = UiState.Loading
                categoryRepository.allCategories
                    .catch { e ->
                        _categoriesState.value =
                            UiState.Error(e.message ?: context.getString(R.string.error_unknown))
                        _categories.value = emptyList()
                    }
                    .collect { categories ->
                        if (categories.isEmpty()) {
                            _categoriesState.value = UiState.Empty
                        } else {
                            _categoriesState.value = UiState.Success(categories)
                        }
                        _categories.value = categories
                    }
            } catch (e: Exception) {
                _categoriesState.value = UiState.Error(e.message ?: context.getString(R.string.error_unknown))
                _categories.value = emptyList()
            }
        }
    }

    fun loadCategoryById(id: Long) {
        viewModelScope.launch {
            try {
                _selectedCategory.value = UiState.Loading
                categoryRepository.getCategoryById(id)
                    .catch { e ->
                        _selectedCategory.value =
                            UiState.Error(e.message ?: context.getString(R.string.error_unknown))
                    }
                    .collect { category ->
                        _selectedCategory.value = UiState.Success(category)
                    }
            } catch (e: Exception) {
                _selectedCategory.value = UiState.Error(e.message ?: context.getString(R.string.error_unknown))
            }
        }
    }

    fun createCategory() {
        viewModelScope.launch {
            val formState = _categoryFormState.value

            if (!validateCategoryForm()) {
                return@launch
            }

            try {
                val category = TransactionCategory(
                    name = formState.name,
                    color = formState.color,
                    icon = formState.iconResId
                )

                categoryRepository.insertCategory(category)

                resetFormState()

                loadCategories()
            } catch (e: Exception) {
            }
        }
    }

    fun updateCategory(id: Long) {
        viewModelScope.launch {
            val formState = _categoryFormState.value

            if (!validateCategoryForm()) {
                return@launch
            }

            try {
                val category = TransactionCategory(
                    id = id,
                    name = formState.name,
                    color = formState.color,
                    icon = formState.iconResId
                )

                categoryRepository.updateCategory(category)

                resetFormState()

                loadCategories()

                loadCategoryById(id)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun deleteCategory(category: TransactionCategory) {
        viewModelScope.launch {
            try {
                categoryRepository.deleteCategory(category)
                loadCategories()
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun createDefaultCategoriesIfNeeded() {
        viewModelScope.launch {
            try {
                categoryRepository.allCategories.collect { categories ->
                    if (categories.isEmpty()) {
                        categoryRepository.insertDefaultCategories(
                            context = context
                        )
                        loadCategories()
                    }
                }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun updateName(name: String) {
        _categoryFormState.value = _categoryFormState.value.copy(
            name = name,
            nameError = if (name.isBlank()) context.getString(R.string.error_name_empty) else null
        )
    }

    fun updateColor(color: Int) {
        _categoryFormState.value = _categoryFormState.value.copy(color = color)
    }

    fun updateIconResId(iconResId: Int) {
        _categoryFormState.value = _categoryFormState.value.copy(iconResId = iconResId)
    }

    fun resetFormState() {
        _categoryFormState.value = CategoryFormState()
    }

    fun initFormWithCategory(category: TransactionCategory) {
        _categoryFormState.value = CategoryFormState(
            name = category.name,
            color = category.color,
            iconResId = category.icon
        )
    }

    private fun validateCategoryForm(): Boolean {
        val formState = _categoryFormState.value

        if (formState.name.isBlank()) {
            _categoryFormState.value = formState.copy(
                nameError = context.getString(R.string.error_name_empty)
            )
            return false
        }

        return true
    }
}

data class CategoryFormState(
    val name: String = "",
    val color: Int = 0xFF4CAF50.toInt(),
    val iconResId: Int = 0,
    val nameError: String? = null
)
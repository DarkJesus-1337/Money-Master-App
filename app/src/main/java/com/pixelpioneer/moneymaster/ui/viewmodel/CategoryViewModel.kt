package com.pixelpioneer.moneymaster.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pixelpioneer.moneymaster.data.model.TransactionCategory
import com.pixelpioneer.moneymaster.data.repository.CategoryRepository
import com.pixelpioneer.moneymaster.util.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class CategoryViewModel(
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    // UI state for categories list
    private val _categoriesState = MutableStateFlow<UiState<List<TransactionCategory>>>(UiState.Loading)
    val categoriesState: StateFlow<UiState<List<TransactionCategory>>> = _categoriesState

    // UI state for a single category (for detail view)
    private val _selectedCategory = MutableStateFlow<UiState<TransactionCategory>>(UiState.Loading)
    val selectedCategory: StateFlow<UiState<TransactionCategory>> = _selectedCategory

    // Form state for adding/editing a category
    private val _categoryFormState = MutableStateFlow(CategoryFormState())
    val categoryFormState: StateFlow<CategoryFormState> = _categoryFormState

    init {
        loadCategories()
    }

    // Load all categories
    private fun loadCategories() {
        viewModelScope.launch {
            try {
                _categoriesState.value = UiState.Loading
                categoryRepository.allCategories
                    .catch { e ->
                        _categoriesState.value = UiState.Error(e.message ?: "Unknown error occurred")
                    }
                    .collect { categories ->
                        if (categories.isEmpty()) {
                            _categoriesState.value = UiState.Empty
                        } else {
                            _categoriesState.value = UiState.Success(categories)
                        }
                    }
            } catch (e: Exception) {
                _categoriesState.value = UiState.Error(e.message ?: "Unknown error occurred")
            }
        }
    }

    // Load a specific category by ID
    fun loadCategoryById(id: Long) {
        viewModelScope.launch {
            try {
                _selectedCategory.value = UiState.Loading
                categoryRepository.getCategoryById(id)
                    .catch { e ->
                        _selectedCategory.value = UiState.Error(e.message ?: "Unknown error occurred")
                    }
                    .collect { category ->
                        _selectedCategory.value = UiState.Success(category)
                    }
            } catch (e: Exception) {
                _selectedCategory.value = UiState.Error(e.message ?: "Unknown error occurred")
            }
        }
    }

    // Create a new category
    fun createCategory() {
        viewModelScope.launch {
            val formState = _categoryFormState.value
            
            // Validate form state
            if (!validateCategoryForm()) {
                return@launch
            }
            
            try {
                // Create a new Category object from form state
                val category = TransactionCategory(
                    name = formState.name,
                    color = formState.color,
                    icon = formState.iconResId
                )
                
                // Insert the category
                categoryRepository.insertCategory(category)
                
                // Reset form state
                resetFormState()
                
                // Reload categories
                loadCategories()
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    // Update an existing category
    fun updateCategory(id: Long) {
        viewModelScope.launch {
            val formState = _categoryFormState.value
            
            // Validate form state
            if (!validateCategoryForm()) {
                return@launch
            }
            
            try {
                // Create a Category object from form state with the existing ID
                val category = TransactionCategory(
                    id = id,
                    name = formState.name,
                    color = formState.color,
                    icon = formState.iconResId
                )
                
                // Update the category
                categoryRepository.updateCategory(category)
                
                // Reset form state
                resetFormState()
                
                // Reload categories
                loadCategories()
                
                // Also reload the selected category if it's being viewed
                loadCategoryById(id)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    // Delete a category
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

    // Create default categories if none exist
    fun createDefaultCategoriesIfNeeded() {
        viewModelScope.launch {
            try {
                categoryRepository.allCategories.collect { categories ->
                    if (categories.isEmpty()) {
                        categoryRepository.insertDefaultCategories()
                        loadCategories()
                    }
                }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    // Update form state when user changes form fields
    fun updateName(name: String) {
        _categoryFormState.value = _categoryFormState.value.copy(
            name = name,
            nameError = if (name.isBlank()) "Name cannot be empty" else null
        )
    }

    fun updateColor(color: Int) {
        _categoryFormState.value = _categoryFormState.value.copy(color = color)
    }

    fun updateIconResId(iconResId: Int) {
        _categoryFormState.value = _categoryFormState.value.copy(iconResId = iconResId)
    }

    // Reset form state
    fun resetFormState() {
        _categoryFormState.value = CategoryFormState()
    }

    // Initialize form state with an existing category (for editing)
    fun initFormWithCategory(category: TransactionCategory) {
        _categoryFormState.value = CategoryFormState(
            name = category.name,
            color = category.color,
            iconResId = category.icon
        )
    }

    // Validate the category form
    private fun validateCategoryForm(): Boolean {
        val formState = _categoryFormState.value
        
        // Check name
        if (formState.name.isBlank()) {
            _categoryFormState.value = formState.copy(
                nameError = "Name cannot be empty"
            )
            return false
        }
        
        return true
    }
}

// Form state for category creation/editing
data class CategoryFormState(
    val name: String = "",
    val color: Int = 0xFF4CAF50.toInt(), // Default to green
    val iconResId: Int = 0, // Default icon will be set in the UI
    val nameError: String? = null
)
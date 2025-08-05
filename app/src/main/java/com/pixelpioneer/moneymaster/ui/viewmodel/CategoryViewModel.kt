package com.pixelpioneer.moneymaster.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pixelpioneer.moneymaster.R
import com.pixelpioneer.moneymaster.data.model.TransactionCategory
import com.pixelpioneer.moneymaster.data.repository.CategoryRepository
import com.pixelpioneer.moneymaster.core.util.UiState
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
 * Handles loading, creating, updating, and deleting categories,
 * as well as managing the category form state and providing UI state flows.
 *
 * @property categoryRepository Repository for category data access.
 */
@HiltViewModel
class CategoryViewModel @Inject constructor(
    private val categoryRepository: CategoryRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _categoriesState =
        MutableStateFlow<UiState<List<TransactionCategory>>>(UiState.Loading)

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
                _categoriesState.value =
                    UiState.Error(e.message ?: context.getString(R.string.error_unknown))
                _categories.value = emptyList()
            }
        }
    }
}

data class CategoryFormState(
    val name: String = "",
    val color: Int = 0xFF4CAF50.toInt(),
    val iconResId: Int = 0,
    val nameError: String? = null
)
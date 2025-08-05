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
 * Initialisiert automatisch vordefinierte Kategorien und repariert die Datenbank.
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
     * Initialisiert die Kategorien und repariert die Datenbank bei der ersten Nutzung.
     */
    private fun initializeAndLoadCategories() {
        viewModelScope.launch {
            try {
                _isInitializing.value = true
                _categoriesState.value = UiState.Loading

                // Initialisiere Standard-Kategorien und repariere die Datenbank
                categoryRepository.initializeDefaultCategoriesAndRepairDatabase()

                // Lade die Kategorien nach der Initialisierung
                loadCategories()

            } catch (e: Exception) {
                // Bei Fehlern: Vordefinierte Kategorien als Fallback verwenden
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
                        // Bei DB-Fehlern: Vordefinierte Kategorien als Fallback verwenden
                        val predefinedCategories = categoryRepository.getPredefinedCategories()
                        _categoriesState.value = UiState.Success(predefinedCategories)
                        _categories.value = predefinedCategories
                    }
                    .collect { categories ->
                        if (categories.isEmpty()) {
                            // Sollte normalerweise nicht auftreten, da wir Fallback haben
                            val predefinedCategories = categoryRepository.getPredefinedCategories()
                            _categoriesState.value = UiState.Success(predefinedCategories)
                            _categories.value = predefinedCategories
                        } else {
                            _categoriesState.value = UiState.Success(categories)
                            _categories.value = categories
                        }
                    }
            } catch (e: Exception) {
                // Als letzter Fallback: Vordefinierte Kategorien verwenden
                val predefinedCategories = categoryRepository.getPredefinedCategories()
                _categoriesState.value = UiState.Success(predefinedCategories)
                _categories.value = predefinedCategories
            }
        }
    }

    /**
     * Erneuert die Kategorien-Liste
     */
    fun refreshCategories() {
        loadCategories()
    }

    /**
     * Erzwingt eine Neuinitialisierung der Datenbank (für Debug-Zwecke)
     */
    fun forceReinitialize() {
        initializeAndLoadCategories()
    }
}

data class CategoryFormState(
    val name: String = "",
    val color: Int = 0xFF4CAF50.toInt(),
    val iconResId: Int = 0,
    val nameError: String? = null
)
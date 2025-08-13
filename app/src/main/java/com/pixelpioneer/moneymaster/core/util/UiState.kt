package com.pixelpioneer.moneymaster.core.util

/**
 * Generic UI state holder for different states of the UI
 *
 * @param T The type of data to be held by the Success state
 */
sealed class UiState<out T> {
    data object Loading : UiState<Nothing>()
    data object Empty : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String) : UiState<Nothing>()
}
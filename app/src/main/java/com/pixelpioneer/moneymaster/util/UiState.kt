package com.pixelpioneer.moneymaster.util

/**
 * Generic UI state holder for different states of the UI
 */
sealed class UiState<out T> {
    object Loading : UiState<Nothing>()
    object Empty : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String) : UiState<Nothing>()
}
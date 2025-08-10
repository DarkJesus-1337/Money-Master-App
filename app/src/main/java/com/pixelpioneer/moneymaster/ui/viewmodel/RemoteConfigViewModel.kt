package com.pixelpioneer.moneymaster.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pixelpioneer.moneymaster.R
import com.pixelpioneer.moneymaster.core.network.RemoteConfigManager
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * State holder for the remote config loading process.
 *
 * @property isLoading Whether the remote config is currently loading.
 * @property isSuccess Whether the remote config was loaded successfully.
 * @property errorMessage Error message if loading failed.
 * @property debugInfo Additional debug information from remote config.
 */
data class RemoteConfigState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null,
    val debugInfo: Map<String, Any> = emptyMap()
)

/**
 * ViewModel for managing remote configuration loading and state.
 *
 * Handles fetching and activating remote config values and exposes loading, success, and error states.
 *
 * @property remoteConfigManager Manager for remote config operations.
 * @property context Application context for accessing resources.
 */
@HiltViewModel
class RemoteConfigViewModel @Inject constructor(
    private val remoteConfigManager: RemoteConfigManager,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(RemoteConfigState())

    init {
        loadRemoteConfig()
    }

    private fun loadRemoteConfig() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            try {
                val success = remoteConfigManager.fetchAndActivate()
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isSuccess = success,
                    debugInfo = remoteConfigManager.getDebugInfo()
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isSuccess = false,
                    errorMessage = e.message ?: context.getString(R.string.error_unknown)
                )
            }
        }
    }
}
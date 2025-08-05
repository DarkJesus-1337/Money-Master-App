package com.pixelpioneer.moneymaster.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pixelpioneer.moneymaster.R
import com.pixelpioneer.moneymaster.core.network.RemoteConfigManager
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RemoteConfigState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null,
    val debugInfo: Map<String, Any> = emptyMap()
)

@HiltViewModel
class RemoteConfigViewModel @Inject constructor(
    private val remoteConfigManager: RemoteConfigManager,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(RemoteConfigState())
    val uiState: StateFlow<RemoteConfigState> = _uiState.asStateFlow()

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
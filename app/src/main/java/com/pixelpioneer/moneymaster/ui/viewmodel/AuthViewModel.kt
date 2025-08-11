package com.pixelpioneer.moneymaster.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pixelpioneer.moneymaster.data.repository.AuthRepository
import com.pixelpioneer.moneymaster.data.repository.AuthState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for managing authentication state and operations
 */
@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()
    
    val authState = authRepository.authState
    
    init {
        // Observe authentication state changes
        viewModelScope.launch {
            authRepository.authState.collect { state ->
                _uiState.value = _uiState.value.copy(
                    isAuthenticated = state is AuthState.Authenticated,
                    currentUser = (state as? AuthState.Authenticated)?.user
                )
            }
        }
    }
    
    /**
     * Register a new user
     */
    fun register(email: String, password: String, confirmPassword: String) {
        // Validation
        if (email.isBlank() || password.isBlank()) {
            _uiState.value = _uiState.value.copy(
                errorMessage = "Email und Passwort dürfen nicht leer sein"
            )
            return
        }
        
        if (password != confirmPassword) {
            _uiState.value = _uiState.value.copy(
                errorMessage = "Passwörter stimmen nicht überein"
            )
            return
        }
        
        if (password.length < 6) {
            _uiState.value = _uiState.value.copy(
                errorMessage = "Passwort muss mindestens 6 Zeichen lang sein"
            )
            return
        }
        
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            
            authRepository.register(email, password)
                .onSuccess { user ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        successMessage = "Registrierung erfolgreich!"
                    )
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = getErrorMessage(exception)
                    )
                }
        }
    }
    
    /**
     * Login user
     */
    fun login(email: String, password: String) {
        // Validation
        if (email.isBlank() || password.isBlank()) {
            _uiState.value = _uiState.value.copy(
                errorMessage = "Email und Passwort dürfen nicht leer sein"
            )
            return
        }
        
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            
            authRepository.login(email, password)
                .onSuccess { user ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        successMessage = "Anmeldung erfolgreich!"
                    )
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = getErrorMessage(exception)
                    )
                }
        }
    }
    
    /**
     * Send password reset email
     */
    fun sendPasswordResetEmail(email: String) {
        if (email.isBlank()) {
            _uiState.value = _uiState.value.copy(
                errorMessage = "Bitte geben Sie Ihre Email-Adresse ein"
            )
            return
        }
        
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            
            authRepository.sendPasswordResetEmail(email)
                .onSuccess {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        successMessage = "Passwort-Reset Email wurde gesendet"
                    )
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = getErrorMessage(exception)
                    )
                }
        }
    }
    
    /**
     * Logout current user
     */
    fun logout() {
        authRepository.logout()
        _uiState.value = AuthUiState()
    }
    
    /**
     * Clear error message
     */
    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
    
    /**
     * Clear success message
     */
    fun clearSuccess() {
        _uiState.value = _uiState.value.copy(successMessage = null)
    }
    
    /**
     * Convert exception to user-friendly error message
     */
    private fun getErrorMessage(exception: Throwable): String {
        return when {
            exception.message?.contains("email", ignoreCase = true) == true -> {
                when {
                    exception.message?.contains("already in use") == true -> 
                        "Diese Email-Adresse wird bereits verwendet"
                    exception.message?.contains("invalid") == true -> 
                        "Ungültige Email-Adresse"
                    exception.message?.contains("not found") == true -> 
                        "Kein Benutzer mit dieser Email-Adresse gefunden"
                    else -> "Email-Fehler: ${exception.message}"
                }
            }
            exception.message?.contains("password", ignoreCase = true) == true -> {
                when {
                    exception.message?.contains("wrong") == true -> 
                        "Falsches Passwort"
                    exception.message?.contains("weak") == true -> 
                        "Passwort ist zu schwach"
                    else -> "Passwort-Fehler: ${exception.message}"
                }
            }
            exception.message?.contains("network", ignoreCase = true) == true -> 
                "Netzwerkfehler. Bitte überprüfen Sie Ihre Internetverbindung"
            else -> exception.message ?: "Ein unbekannter Fehler ist aufgetreten"
        }
    }
}

/**
 * UI State for authentication screens
 */
data class AuthUiState(
    val isLoading: Boolean = false,
    val isAuthenticated: Boolean = false,
    val currentUser: com.google.firebase.auth.FirebaseUser? = null,
    val errorMessage: String? = null,
    val successMessage: String? = null
)
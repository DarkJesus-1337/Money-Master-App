package com.pixelpioneer.moneymaster.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for handling Firebase Authentication operations.
 * 
 * Manages user authentication state, login, registration, and logout operations.
 */
@Singleton
class AuthRepository @Inject constructor() {
    
    private val auth: FirebaseAuth = Firebase.auth
    
    companion object {
        private const val TAG = "AuthRepository"
    }
    
    /**
     * Flow that emits the current authentication state
     */
    val authState: Flow<AuthState> = callbackFlow {
        val authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            val state = if (user != null) {
                AuthState.Authenticated(user)
            } else {
                AuthState.Unauthenticated
            }
            trySend(state)
        }
        
        auth.addAuthStateListener(authStateListener)
        
        awaitClose {
            auth.removeAuthStateListener(authStateListener)
        }
    }
    
    /**
     * Get the current authenticated user
     */
    val currentUser: FirebaseUser?
        get() = auth.currentUser
    
    /**
     * Check if a user is currently authenticated
     */
    val isAuthenticated: Boolean
        get() = currentUser != null
    
    /**
     * Register a new user with email and password
     * 
     * @param email User's email address
     * @param password User's password
     * @return Result containing success or error message
     */
    suspend fun register(email: String, password: String): Result<FirebaseUser> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            result.user?.let {
                Timber.tag(TAG).d("User registered successfully: ${it.uid}")
                Result.success(it)
            } ?: Result.failure(Exception("Registration failed: User is null"))
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "Registration failed")
            Result.failure(e)
        }
    }
    
    /**
     * Login user with email and password
     * 
     * @param email User's email address
     * @param password User's password
     * @return Result containing success or error message
     */
    suspend fun login(email: String, password: String): Result<FirebaseUser> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            result.user?.let {
                Timber.tag(TAG).d("User logged in successfully: ${it.uid}")
                Result.success(it)
            } ?: Result.failure(Exception("Login failed: User is null"))
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "Login failed")
            Result.failure(e)
        }
    }
    
    /**
     * Send password reset email
     * 
     * @param email User's email address
     * @return Result indicating success or failure
     */
    suspend fun sendPasswordResetEmail(email: String): Result<Unit> {
        return try {
            auth.sendPasswordResetEmail(email).await()
            Timber.tag(TAG).d("Password reset email sent to: $email")
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "Failed to send password reset email")
            Result.failure(e)
        }
    }
    
    /**
     * Logout the current user
     */
    fun logout() {
        auth.signOut()
        Timber.tag(TAG).d("User logged out")
    }
    
    /**
     * Update user profile information
     * 
     * @param displayName User's display name
     * @return Result indicating success or failure
     */
    suspend fun updateProfile(displayName: String): Result<Unit> {
        return try {
            currentUser?.let { user ->
                val profileUpdates = com.google.firebase.auth.UserProfileChangeRequest.Builder()
                    .setDisplayName(displayName)
                    .build()
                
                user.updateProfile(profileUpdates).await()
                Timber.tag(TAG).d("User profile updated")
                Result.success(Unit)
            } ?: Result.failure(Exception("No authenticated user"))
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "Failed to update profile")
            Result.failure(e)
        }
    }
    
    /**
     * Delete the current user account
     * 
     * @return Result indicating success or failure
     */
    suspend fun deleteAccount(): Result<Unit> {
        return try {
            currentUser?.delete()?.await()
            Timber.tag(TAG).d("User account deleted")
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "Failed to delete account")
            Result.failure(e)
        }
    }
}

/**
 * Represents the authentication state of the app
 */
sealed class AuthState {
    data class Authenticated(val user: FirebaseUser) : AuthState()
    object Unauthenticated : AuthState()
    object Loading : AuthState()
}
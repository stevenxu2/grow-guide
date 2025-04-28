package com.xxu.growguide.viewmodels

import android.app.Application
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.xxu.growguide.api.PlantsManager
import com.xxu.growguide.auth.AuthManager
import com.xxu.growguide.data.entity.UserEntity
import com.xxu.growguide.ui.viewmodels.GardenPlantDetailViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Purpose: ViewModel for authentication related functionality
 *
 */
class AuthViewModel(
    private val authManager: AuthManager
) : ViewModel() {

    // UI State
    private val _showLoginDialog = MutableStateFlow(false)
    val showLoginDialog: StateFlow<Boolean> = _showLoginDialog.asStateFlow()

    // Expose auth state from manager
    val isLoggedIn = authManager.isLoggedIn
    val authError = authManager.authError
    val isLoading = authManager.isLoading
    val isAnonymous = authManager.isAnonymous

    // User data
    val currentUser = authManager.currentUser

    /**
     * Purpose: Show the login dialog
     */
    fun showLogin() {
        _showLoginDialog.value = true
    }

    /**
     * Purpose: Hide the login dialog
     */
    fun hideLogin() {
        _showLoginDialog.value = false
    }

    /**
     * Purpose: Sign in with email and password
     */
    suspend fun signIn(email: String, password: String): Boolean {
        return authManager.signIn(email, password)
    }

    /**
     * Purpose: Sign in anonymously
     */
    suspend fun signInAnonymously(): Boolean {
        return authManager.signInAnonymously()
    }

    /**
     * Purpose: Sign up with email, password and display name
     */
    suspend fun signUp(email: String, password: String, displayName: String): Boolean {
        return authManager.signUp(email, password, displayName)
    }

    /**
     * Purpose: Convert anonymous account to permanent account
     */
    suspend fun convertAnonymousAccount(email: String, password: String, displayName: String): Boolean {
        return authManager.convertAnonymousUser(email, password, displayName)
    }

    /**
     * Purpose: Sign out the current user
     */
    fun signOut() {
        authManager.signOut()
    }

    /**
     * Purpose: Get current user data synchronously
     */
    suspend fun getCurrentUser(): UserEntity? {
        return authManager.getCurrentUserFromDb()
    }

    fun clearErrors() {
        authError.value = null
        isLoading.value = false
    }
}

/**
 * Purpose: Factory for creating AuthViewModel instances
 */
class AuthViewModelFactory(
    private val authManager: AuthManager
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AuthViewModel(authManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
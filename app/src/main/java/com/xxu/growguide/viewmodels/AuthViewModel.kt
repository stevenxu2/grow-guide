package com.xxu.growguide.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.xxu.growguide.auth.AuthManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Purpose: ViewModel for authentication related functionality
 *
 * Manages user authentication state and login dialog visibility
 *
 * @param application Application context used to initialize the auth manager
 */
class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val authManager = AuthManager.getInstance(application.applicationContext)

    // UI State
    private val _showLoginDialog = MutableStateFlow(false)
    val showLoginDialog: StateFlow<Boolean> = _showLoginDialog.asStateFlow()

    // Expose auth state from manager
    val isLoggedIn = authManager.isLoggedIn
    val currentUser = authManager.currentUser

    /**
     * Purpose: Show the login dialog
     *
     * Sets the dialog visibility state to true
     */
    fun showLogin() {
        _showLoginDialog.value = true
    }

    /**
     * Purpose: Hide the login dialog
     *
     * Sets the dialog visibility state to false
     */
    fun hideLogin() {
        _showLoginDialog.value = false
    }

    /**
     * Purpose: Sign out the current user
     *
     * Delegates to the AuthManager to handle the sign out process
     */
    fun signOut() {
        authManager.signOut()
    }

    /**
     * Purpose: Get the auth manager instance
     *
     * @return The AuthManager singleton instance
     */
    fun getAuthManager() = authManager
}
package com.xxu.growguide.viewmodels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

/**
 * Purpose: ViewModel that manages the application theme state
 */
class ThemeViewModel : ViewModel() {
    // observe changes
    val isDarkTheme = mutableStateOf(false)

    fun toggleTheme() {
        isDarkTheme.value = !isDarkTheme.value
    }
}
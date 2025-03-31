package com.xxu.growguide.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.xxu.growguide.api.WeatherManager
import com.xxu.growguide.data.models.weather.WeatherData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

/**
 * View model for weather-related functionality
 */
class WeatherViewModel(
    private val weatherManager: WeatherManager
) : ViewModel() {

    // UI states
    private val _weatherState = MutableStateFlow<WeatherUiState>(WeatherUiState.Loading)
    val weatherState: StateFlow<WeatherUiState> = _weatherState

    /**
     * Fetch current weather for user's location
     */
    fun fetchCurrentWeather() {
        viewModelScope.launch {
            _weatherState.value = WeatherUiState.Loading

            try {
                weatherManager.getCurrentWeather()
                    .catch { e ->
                        _weatherState.value = WeatherUiState.Error(e.message ?: "Unknown error")
                    }
                    .collect { weatherData ->
                        _weatherState.value = WeatherUiState.Success(weatherData)
                    }
            } catch (e: Exception) {
                _weatherState.value = WeatherUiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    /**
     * Refresh weather data
     */
    fun refreshWeather() {
        fetchCurrentWeather()
    }
}

/**
 * UI state for weather data
 */
sealed class WeatherUiState {
    data object Loading : WeatherUiState()
    data class Success(val weatherData: WeatherData) : WeatherUiState()
    data class Error(val message: String) : WeatherUiState()
}

/**
 * Factory for creating WeatherViewModel
 */
class WeatherViewModelFactory(
    private val weatherManager: WeatherManager
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WeatherViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return WeatherViewModel(weatherManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
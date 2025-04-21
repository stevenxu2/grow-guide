package com.xxu.growguide.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.xxu.growguide.api.PlantsManager
import com.xxu.growguide.data.database.PlantsDao
import com.xxu.growguide.data.entity.PlantsEntity
import com.xxu.growguide.data.entity.UserPlantsEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

/**
 * Purpose: ViewModel that manages the data and state for the plant detail screen
 *
 * @param plantsManager Manager that handles fetching plant data from API
 * @param plantsDao Data access object for local plant database operations
 */
class AddPlantViewModel(
    private val plantsManager: PlantsManager
) : ViewModel() {

    // State for the plant detail
    private val _plantDetail = MutableStateFlow<PlantsEntity?>(null)
    val plantDetail: StateFlow<PlantsEntity?> = _plantDetail

    // State for loading
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    // State for error
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    /**
     * Purpose: Load plant detail from database or API
     *
     * Attempts to fetch plant details from the API first, which will also update the local database.
     * Updates loading state, error state, and plant detail state accordingly.
     *
     * @param plantId The unique identifier of the plant to load
     */
    fun loadPlantDetail(plantId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                // Try to get plant from API and then from the DB (which will be updated by the API call)
                plantsManager.getPlantDetail(plantId)
                    .flowOn(Dispatchers.IO)
                    .catch { e ->
                        Log.e("AddPlantViewModel", "API error: ${e.message}")
                    }
                    .collect { plant ->
                        _plantDetail.value = plant
                        _isLoading.value = false
                    }
            } catch (e: Exception) {
                Log.e("AddPlantViewModel", "Error loading plant detail: ${e.message}")
                _error.value = "Failed to load plant details: ${e.message}"
                _isLoading.value = false
            }
        }
    }

    fun addUserPlant(plant: UserPlantsEntity?) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                plantsManager.saveUserPlant(plant)
                _isLoading.value = false
                Log.i("AddPlantViewModel", "Successfully save user plant to database!")
            } catch (e: Exception) {
                _isLoading.value = false
                _error.value = "Failed to save plant to garden: ${e.message}"
            }
        }
    }
}

/**
 * Purpose: Factory for creating PlantDetailViewModel instances
 *
 * @param plantsManager Manager that handles fetching plant data from API
 */
class AddPlantViewModelFactory(
    private val plantsManager: PlantsManager
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddPlantViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AddPlantViewModel(plantsManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
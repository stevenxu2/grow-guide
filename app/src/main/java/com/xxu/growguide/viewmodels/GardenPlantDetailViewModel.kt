package com.xxu.growguide.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.xxu.growguide.api.PlantsManager
import com.xxu.growguide.data.database.UserPlantsDao
import com.xxu.growguide.data.entity.PlantsEntity
import com.xxu.growguide.data.entity.UserPlantsEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

/**
 * Purpose: ViewModel that manages user plants data and operations
 *
 * @param userPlantDao Data access object for user plants database operations
 */
class GardenPlantDetailViewModel(
    private val plantsManager: PlantsManager
) : ViewModel() {

    // State for a list of plants in user's garden
    private val _gardenPlantDetail = MutableStateFlow<UserPlantsEntity?>(null)
    val gardenPlantDetail: StateFlow<UserPlantsEntity?> = _gardenPlantDetail

    // State for a plant detail
    private val _plantDetail = MutableStateFlow<PlantsEntity?>(null)
    val plantDetail: StateFlow<PlantsEntity?> = _plantDetail

    // State for error message
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    // State for loading status
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    // State for deleting garden plant
    private val _deletingSuccess = MutableStateFlow(false)
    val deletingSuccess: StateFlow<Boolean> = _deletingSuccess

    // State for watering garden plant
    private val _wateringSuccess = MutableStateFlow(false)
    val wateringSuccess: StateFlow<Boolean> = _wateringSuccess


    /**
     * Purpose: Get all plants for a specific user
     *
     * @param userId The ID of the user whose plants to retrieve
     * @return A flow of user plants for the specified user
     */
    fun getGardenPlantWithDetail(userPlantId: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                plantsManager.getUserPlantDetail(userPlantId)
                    .flowOn(Dispatchers.IO)
                    .catch { e ->
                        Log.e("GardenPlantDetailViewModel", "Error loading user plant detail: ${e.message}", e)
                        _error.value = "Failed to load garden plant detail: ${e.message}"
                        _isLoading.value = false
                    }
                    .collect { userPlantDetail ->
                        _gardenPlantDetail.value = userPlantDetail
                    }
                plantsManager.getPlantDetail(_gardenPlantDetail.value?.plantId ?: 0)
                    .flowOn(Dispatchers.IO)
                    .catch { e ->
                        Log.e("GardenPlantDetailViewModel", "Error loading plant detail: ${e.message}", e)
                        _error.value = "Failed to load plant detail: ${e.message}"
                        _isLoading.value = false
                    }
                    .collect { plantDetail ->
                        _plantDetail.value = plantDetail
                        _isLoading.value = false
                    }
            } catch (e: Exception) {
                Log.e("GardenPlantDetailViewModel", "Error in getGardenPlantDetail: ${e.message}", e)
                _error.value = "Failed to load garden plant detail: ${e.message}"
                _isLoading.value = false
            }
        }
    }

    /**
     * Purpose: Load plant detail from database or API
     *
     * Attempts to fetch plant details from the API first, which will also update the local database.
     * Updates loading state, error state, and plant detail state accordingly.
     *
     * @param plantId The unique identifier of the plant to load
     */
    fun getPlantDetail(plantId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                // Try to get plant from API and then from the DB (which will be updated by the API call)
                plantsManager.getPlantDetail(plantId)
                    .flowOn(Dispatchers.IO)
                    .catch { e ->
                        Log.e("GardenPlantDetailViewModel", "API error: ${e.message}")
                    }
                    .collect { plant ->
                        _plantDetail.value = plant
                        _isLoading.value = false
                    }
            } catch (e: Exception) {
                Log.e("GardenPlantDetailViewModel", "Error loading plant detail: ${e.message}")
                _error.value = "Failed to load plant details: ${e.message}"
                _isLoading.value = false
            }
        }
    }

    /**
     * Purpose: Record when a plant is watered by updating the lastWateredDate
     *
     * @param userPlantId The ID of the user plant that was watered
     */
    fun recordWatering(userPlantId: Long?) {
        viewModelScope.launch {
            _error.value = null
            _wateringSuccess.value = false

            try {
                // Update watering timestamp through PlantManager
                val success = userPlantId?.let { plantsManager.recordPlantWatering(it) }

                if (success == true) {
                    _wateringSuccess.value = true
                } else {
                    Log.e("GardenPlantDetailViewModel", "Error recording watering")
                    _error.value = "Failed to update watering"
                }
            } catch (e: Exception) {
                Log.e("GardenPlantDetailViewModel", "Error recording watering: ${e.message}", e)
                _error.value = "Failed to update watering: ${e.message}"
            }
        }
    }

    /**
     * Purpose: Remove a plant from the user's garden
     *
     * @param userPlantId The ID of the user plant to remove
     */
    fun removeUserPlant(userPlantId: Long) {
        viewModelScope.launch {
            _error.value = null
            _deletingSuccess.value = false

            try {
                val success = plantsManager.removeUserPlant(userPlantId)
                if (success) {
                    _deletingSuccess.value = true
                } else {
                    Log.e("GardenPlantDetailViewModel", "Error removing plant")
                    _error.value = "Failed to remove plant"
                }
            } catch (e: Exception) {
                Log.e("GardenPlantDetailViewModel", "Error removing plant: ${e.message}", e)
                _error.value = "Failed to remove plant: ${e.message}"
            }
        }
    }
}

/**
 * Purpose: Factory for creating UserPlantViewModel instances
 */
class GardenPlantDetailViewModelFactory(
    private val plantsManager: PlantsManager
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GardenPlantDetailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return GardenPlantDetailViewModel(plantsManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
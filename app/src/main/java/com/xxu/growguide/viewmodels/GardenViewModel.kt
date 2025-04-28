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
 * Data class that combines user plant data with the associated plant details
 */
data class UserPlantWithDetails(
    val userPlant: UserPlantsEntity,
    val plant: PlantsEntity
)

/**
 * Purpose: ViewModel that manages user plants data and operations
 *
 * @param userPlantDao Data access object for user plants database operations
 */
class GardenViewModel(
    private val plantsManager: PlantsManager
) : ViewModel() {

    // State for a list of plants in user's garden
    private val _gardenPlants = MutableStateFlow<List<UserPlantWithDetails?>>(emptyList())
    val gardenPlants: StateFlow<List<UserPlantWithDetails?>> = _gardenPlants

    // State for error message
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    // State for loading status
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading


    /**
     * Purpose: Get all plants for a specific user
     *
     * @param userId The ID of the user whose plants to retrieve
     * @return A flow of user plants for the specified user
     */
    fun getGardenPlants(userId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                plantsManager.getUserPlants(userId)
                    .flowOn(Dispatchers.IO)
                    .catch { e ->
                        Log.e("GardenViewModel", "Error loading user plants: ${e.message}", e)
                        _error.value = "Failed to load garden: ${e.message}"
                        _isLoading.value = false
                    }
                    .collect { userPlantsWithDetails ->
                        _gardenPlants.value = userPlantsWithDetails
                        _isLoading.value = false
                    }
            } catch (e: Exception) {
                Log.e("GardenViewModel", "Error in getGardenPlants: ${e.message}", e)
                _error.value = "Failed to load garden: ${e.message}"
                _isLoading.value = false
            }
        }
    }

    /**
     * Purpose: Record when a plant is watered by updating the lastWateredDate
     *
     * @param userPlantId The ID of the user plant that was watered
     */
    fun recordWatering(userPlantId: Long) {
        viewModelScope.launch {
            try {
                // Find the user plant to get its userId
                val userPlant = _gardenPlants.value.find { it?.userPlant?.userPlantId == userPlantId }?.userPlant
                    ?: return@launch

                val userId = userPlant.userId

                // Update watering timestamp through PlantManager
                val success = plantsManager.recordPlantWatering(userPlantId)

                if (success) {
                    // Refresh the garden to show updated watering info
                    getGardenPlants(userId)
                    Log.d("GardenViewModel", "Plant watered: $userPlantId")
                } else {
                    _error.value = "Failed to update watering"
                }
            } catch (e: Exception) {
                Log.e("GardenViewModel", "Error recording watering: ${e.message}", e)
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
            try {
                // Find the user plant to get its userId
                val userPlant = _gardenPlants.value.find { it?.userPlant?.userPlantId == userPlantId }?.userPlant
                    ?: return@launch

                val userId = userPlant.userId

                // Remove through PlantManager
                val success = plantsManager.removeUserPlant(userPlantId)

                if (success) {
                    // Refresh the garden
                    getGardenPlants(userId)
                    Log.d("GardenViewModel", "Plant removed: $userPlantId")
                } else {
                    _error.value = "Failed to remove plant"
                }
            } catch (e: Exception) {
                Log.e("GardenViewModel", "Error removing plant: ${e.message}", e)
                _error.value = "Failed to remove plant: ${e.message}"
            }
        }
    }

    /**
     * Purpose: Clear any error messages
     */
    fun clearError() {
        _error.value = null
    }
}

/**
 * Purpose: Factory for creating UserPlantViewModel instances
 *
 * @param userPlantDao Data access object for user plants database operations
 */
class GardenViewModelFactory(
    private val plantsManager: PlantsManager
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GardenViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return GardenViewModel(plantsManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
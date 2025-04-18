package com.xxu.growguide.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.xxu.growguide.data.database.UserPlantsDao
import com.xxu.growguide.data.entity.UserPlantsEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * Purpose: ViewModel that manages user plants data and operations
 *
 * @param userPlantDao Data access object for user plants database operations
 */
class UserPlantViewModel(
    private val userPlantDao: UserPlantsDao
) : ViewModel() {

    // State for operation success
    private val _operationSuccess = MutableStateFlow<Boolean?>(null)
    val operationSuccess: StateFlow<Boolean?> = _operationSuccess

    // State for error message
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    // State for loading status
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    /**
     * Purpose: Add a plant to the user's garden
     *
     * @param userPlant The user plant to add
     */
    fun addPlantToGarden(userPlant: UserPlantsEntity) {
        viewModelScope.launch {
            _isLoading.value = true
            _operationSuccess.value = null
            _error.value = null

            try {
                // Check if the plant is already in the user's garden
                val isAlreadyInGarden = userPlantDao.isPlantInUserGarden(
                    userId = userPlant.userId,
                    plantId = userPlant.plantId
                )

                if (isAlreadyInGarden) {
                    _error.value = "This plant is already in your garden"
                    _operationSuccess.value = false
                } else {
                    val id = userPlantDao.insertUserPlant(userPlant)
                    Log.d("UserPlantViewModel", "Plant added to garden with ID: $id")
                    _operationSuccess.value = true
                }
            } catch (e: Exception) {
                Log.e("UserPlantViewModel", "Error adding plant to garden: ${e.message}", e)
                _error.value = "Failed to add plant to garden: ${e.message}"
                _operationSuccess.value = false
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Purpose: Get all plants for a specific user
     *
     * @param userId The ID of the user whose plants to retrieve
     * @return A flow of user plants for the specified user
     */
    fun getUserPlants(userId: String): Flow<List<UserPlantsEntity>> {
        return userPlantDao.getUserPlants(userId)
    }

    /**
     * Purpose: Clear operation state after it's been processed
     */
    fun clearOperationState() {
        _operationSuccess.value = null
        _error.value = null
    }
}

/**
 * Purpose: Factory for creating UserPlantViewModel instances
 *
 * @param userPlantDao Data access object for user plants database operations
 */
class UserPlantViewModelFactory(
    private val userPlantDao: UserPlantsDao
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserPlantViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return UserPlantViewModel(userPlantDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
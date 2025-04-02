package com.xxu.growguide.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.xxu.growguide.api.PlantManager
import com.xxu.growguide.data.database.PlantsDao
import com.xxu.growguide.data.entity.PlantsEntity
import com.xxu.growguide.data.models.plants.PlantData
import com.xxu.growguide.data.models.plants.list.Data
import com.xxu.growguide.data.models.plants.list.PlantsListData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

/**
 * Purpose: ViewModel that manages the data and state for the plants list screen
 *
 * @param plantManager Manager that handles fetching plant data from API
 * @param plantsDao Data access object for local plant database operations
 */
class PlantsViewModel(
    private val plantManager: PlantManager,
    private val plantsDao: PlantsDao
) : ViewModel() {

    // State for the list of plants
    private val _plants = MutableStateFlow<List<Data?>>(emptyList())
    val plants: StateFlow<List<Data?>> = _plants

    // State for search query
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    // State for loading
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    // State for error
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    // State for list scroll
    private val _resetScroll = MutableStateFlow<Boolean>(false)
    val resetScroll: StateFlow<Boolean> = _resetScroll

    // Current page for pagination
    private var currentPage = 1

    // Flag to prevent multiple simultaneous pagination requests
    private var isLoadingNextPage = false

    // Flag to indicate if there are more pages to load
    private var hasMorePages = true

    init {
        // Load initial data
        searchPlants("")
    }

    /**
     * Purpose: Search plants from API based on a query string
     *
     * Resets to page 1 when a new search query is provided.
     *
     * @param query The search term to filter plants (defaults to empty string)
     */
    fun searchPlants(query: String = "") {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            _searchQuery.value = query
            if (query.isNotEmpty()) currentPage = 1
            fetchPlantsFromApi(query)
        }
    }

    /**
     * Purpose: Fetch plants from API based on query and current page
     *
     * @param query The search term to filter plants
     */
    private suspend fun fetchPlantsFromApi(query: String) {
        Log.i("PlantsViewModel", "currentPage: ${currentPage}")
        try {
            plantManager.getPlantsList(query = query, page = currentPage)
                .catch { e ->
                    Log.e("PlantsViewModel", "API error: ${e.message}")
                    _error.value = "Failed to fetch plants from server: ${e.message}"
                    _isLoading.value = false
                }
                .collect { plants ->
                    if (plants.data?.isNotEmpty() == true){
                        _plants.value = plants.data as List<Data>
                        _isLoading.value = false
                        _resetScroll.value = true
                    }
                }
        } catch (e: Exception) {
            Log.e("PlantsViewModel", "Error in fetchPlantsFromApi: ${e.message}")
            _error.value = "Failed to fetch plants: ${e.message}"
            _isLoading.value = false
        }
    }

    /**
     * Purpose: Load more plants by fetching the next page from the API
     *
     * Handles pagination by appending new data to the existing list.
     * Updates loading state and tracks if more pages are available.
     */
    fun loadMorePlants() {
        // Don't load if already loading, if there are no more pages, or if we're on the first load
        if (isLoadingNextPage || !hasMorePages || _isLoading.value) {
            return
        }

        isLoadingNextPage = true
        _isLoading.value = true

        viewModelScope.launch {
            val nextPage = currentPage + 1

            plantManager.getPlantsList(query = _searchQuery.value, page = nextPage)
                .catch { exception ->
                    _error.value = exception.message
                    _isLoading.value = false
                    isLoadingNextPage = false
                }
                .collect { response ->
                    // Append new data to existing list
                    val newData = response.data ?: emptyList()
                    if (newData.isNotEmpty()) {
                        _plants.value += newData
                        currentPage = nextPage

                        // Check if there are more pages
                        hasMorePages = response.total?.let { total ->
                            val perPage = response.perPage ?: 30
                            (currentPage * perPage) < total
                        } ?: false
                    } else {
                        hasMorePages = false
                    }

                    _isLoading.value = false
                    isLoadingNextPage = false
                }
        }
    }

    /**
     * Purpose: Update search query without triggering a search
     *
     * @param query The new search query to store
     */
    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    /**
     * Purpose: Reset the scroll reset flag after scrolling to top
     *
     * Called after the UI has processed the scroll reset.
     */
    fun clearScrollReset() {
        _resetScroll.value = false
    }
}

/**
 * Purpose: Factory for creating PlantsViewModel instances
 *
 * @param plantManager Manager that handles fetching plant data from API
 * @param plantsDao Data access object for local plant database operations
 */
class PlantsViewModelFactory(
    private val plantManager: PlantManager,
    private val plantsDao: PlantsDao
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PlantsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PlantsViewModel(plantManager, plantsDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
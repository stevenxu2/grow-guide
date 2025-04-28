package com.xxu.growguide.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Purpose: Mock implementation of PlantsViewModel for testing
 *
 * This class simulates the behavior of PlantsViewModel without making actual API calls.
 */
class MockPlantsViewModel : ViewModel() {

    // State for the list of plants
    private val _plants = MutableStateFlow<List<Data?>>(generateMockPlants())
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

    // Mock pagination data
    private var currentPage = 1
    private var hasMorePages = true
    private var isLoadingNextPage = false

    /**
     * Purpose: Search plants based on a query string
     *
     * In this mock implementation, we filter the mock data based on the query.
     *
     * @param query The search term to filter plants
     */
    fun searchPlants(query: String = "") {
        _isLoading.value = true
        _error.value = null
        _searchQuery.value = query
        currentPage = 1

        // Simulate network delay
        Thread.sleep(500)

        // Filter mock plants based on query
        val filteredPlants = if (query.isEmpty()) {
            generateMockPlants()
        } else {
            generateMockPlants().filter { plant ->
                plant.commonName?.contains(query, true) == true ||
                        plant.scientificName?.any { it.contains(query, true) } == true
            }
        }

        _plants.value = filteredPlants
        _isLoading.value = false
        _resetScroll.value = true
    }

    /**
     * Purpose: Load more plants for pagination
     *
     * In this mock implementation, we simulate loading additional pages.
     */
    fun loadMorePlants() {
        // Don't load if already loading, if there are no more pages, or if we're on the first load
        if (isLoadingNextPage || !hasMorePages || _isLoading.value) {
            return
        }

        isLoadingNextPage = true
        _isLoading.value = true

        // Simulate network delay
        Thread.sleep(800)

        // Only add more plants if we haven't reached page 3 (arbitrary limit for mock)
        if (currentPage < 3) {
            currentPage++
            val additionalPlants = generateMockPlants(currentPage)
            _plants.value = _plants.value + additionalPlants
            hasMorePages = currentPage < 3
        } else {
            hasMorePages = false
        }

        _isLoading.value = false
        isLoadingNextPage = false
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
     */
    fun clearScrollReset() {
        _resetScroll.value = false
    }

    /**
     * Purpose: Trigger a mock error state
     */
    fun triggerError(errorMessage: String = "Mock error message") {
        _error.value = errorMessage
    }

    /**
     * Purpose: Generate mock plant data
     *
     * @param page Page number to determine which set of mock data to generate
     * @return List of mock plant data items
     */
    private fun generateMockPlants(page: Int = 1): List<Data> {
        val offset = (page - 1) * 10
        return (1..10).map { index ->
            val id = offset + index
            Data(
                id = id,
                commonName = "Mock Plant ${id}",
                scientificName = listOf("Mockus plantus ${id}", "Alternativus mockus"),
                defaultImage = DefaultImage(
                    smallUrl = "https://example.com/plant${id}-small.jpg",
                    regularUrl = "https://example.com/plant${id}.jpg"
                ),
                family = "Mockaceae",
                genus = "Mockus",
                slug = "mock-plant-${id}"
            )
        }
    }
}

/**
 * Purpose: Factory for creating MockPlantsViewModel instances
 *
 * This factory allows you to inject the mock ViewModel using the same pattern
 * as the real ViewModel, making it easier to swap implementations in tests.
 */
class MockPlantsViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MockPlantsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MockPlantsViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

/**
 * Mock Data class to match the structure in the original model
 * Note: This is a simplified version with only the essential fields
 */
data class Data(
    val id: Int,
    val commonName: String?,
    val scientificName: List<String>? = listOf("Mockus plantus"),
    val defaultImage: DefaultImage? = DefaultImage("https://example.com/plant-small.jpg", "https://example.com/plant.jpg"),
    val family: String? = null,
    val genus: String? = null,
    val slug: String? = null
)

/**
 * Mock class for DefaultImage in plant data
 */
data class DefaultImage(
    val smallUrl: String?,
    val regularUrl: String?
)
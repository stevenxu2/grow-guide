package com.xxu.growguide.api

import com.xxu.growguide.data.models.plants.PlantData
import com.xxu.growguide.data.models.plants.guides.PlantGuideData
import com.xxu.growguide.data.models.plants.list.PlantsListData
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Purpose: Interface for perenual.com API calls
 */
interface PlantsApiService {
    /**
     * Purpose: Get species list
     * Endpoint: https://perenual.com/api/v2/species-list
     */
    @GET("v2/species-list")
    suspend fun getPlantsList(
        @Query("key") apiKey: String,
        @Query("q") query: String,
        @Query("page") page: String
    ): PlantsListData

    /**
     * Purpose: Get the detail of a plant
     * Endpoint: https://perenual.com/api/v2/species/details/{id}
     */
    @GET("v2/species/details/{id}")
    suspend fun getPlantDetail(
        @Path("id") id: Int,
        @Query("key") apiKey: String
    ): PlantData

    /**
     * Purpose: Get the guide of a plant
     * Endpoint: https://perenual.com/api/species-care-guide-list
     */
    @GET("species-care-guide-list")
    suspend fun getPlantGuide(
        @Query("key") apiKey: String,
        @Query("species_id") id: Int
    ): PlantGuideData
}
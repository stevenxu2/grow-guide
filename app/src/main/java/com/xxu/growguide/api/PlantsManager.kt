package com.xxu.growguide.api

import android.content.Context
import com.xxu.growguide.data.database.PlantsDao
import com.xxu.growguide.data.entity.PlantsEntity
import com.xxu.growguide.data.models.plants.DefaultImage
import com.xxu.growguide.data.models.plants.PlantData
import com.xxu.growguide.data.models.plants.list.PlantsListData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

/**
 * Purpose: Manager class that handles fetching and caching plant data
 */
class PlantManager(
    private val plantsApiService: PlantsApiService,
    private val plantsDao: PlantsDao,
    private val context: Context
) {
    /**
     * Purpose: Get a list of plants with optional search query
     *
     * @param query The search query (empty for all plants)
     * @param page The page number for pagination
     */
    fun getPlantsList(query: String = "", page: Int = 1): Flow<PlantsListData> = flow {
        try {
            val apiKey = API.getPlantsApiKey()
            val response = plantsApiService.getPlantsList(
                apiKey = apiKey,
                query = query,
                page = page.toString()
            )

            emit(response)
        } catch (e: Exception) {
            throw e
        }
    }.flowOn(Dispatchers.IO)

    /**
     * Purpose: Get detailed information about a specific plant
     *
     * @param id The ID of the plant
     */
    fun getPlantDetail(id: Int): Flow<PlantsEntity?> = flow {
        try {
            // Check cache first
            val cachedPlant = plantsDao.getPlantById(id)

            // if cache exists
            if (cachedPlant != null) {
                emit(cachedPlant)
                return@flow
            }

            // Fetch from network
            val apiKey = API.getPlantsApiKey()
            val response = plantsApiService.getPlantDetail(
                apiKey = apiKey,
                id = id
            )

            // Cache the detailed data
            val plantsEntity = mapPlantDataToEntity(response)
            if (plantsEntity != null) {
                plantsDao.insertPlant(plantsEntity)
            }

            emit(plantsEntity)
        } catch (e: Exception) {
            throw e
        }
    }.flowOn(Dispatchers.IO)


    /**
     * Purpose: Map PlantData model to PlantsEntity for database storage
     *
     * @param data The plant data object from the API response that needs to be converted
     * @return PlantsEntity object ready for database storage, or null if the plant ID is missing
     */
    private fun mapPlantDataToEntity(data: PlantData): PlantsEntity? {
        val id = data.id ?: return null

        return PlantsEntity(
            id = id,
            commonName = data.commonName ?: "Unknown",
            scientificName = data.scientificName?.joinToString(", ") ?: "",
            family = data.family?.toString() ?: "",
            genus = data.genus ?: "",
            imageUrl = data.defaultImage?.originalUrl ?: "",
            description = data.description ?: "",
            cycle = data.cycle ?: "",
            watering = data.watering ?: "",
            sunlight = data.sunlight?.joinToString(", ") ?: "",
            timestamp = System.currentTimeMillis(),
            type = data.type ?: "",
            maintenance = data.maintenance.toString(),
            growthRate = data.growthRate ?: "",
            droughtTolerant = data.droughtTolerant ?: false,
            saltTolerant = data.saltTolerant ?: false,
            indoor = data.indoor ?: false,
            careLevel = data.careLevel ?: "",
            flowers = data.flowers ?: false,
            cones = data.cones ?: false,
            fruits = data.fruits ?: false,
            leaf = data.leaf ?: false
        )
    }

    /**
     * Purpose: Maps a PlantsEntity from the local database to a PlantData model for use in the app
     *
     * @param entity The plant entity retrieved from the local database
     * @return PlantData object with all available plant information
     */
    private fun mapEntityToPlantData(entity: PlantsEntity): PlantData {
        // Create default image
        val defaultImage = DefaultImage(
            originalUrl = entity.imageUrl,
            regularUrl = null,
            mediumUrl = null,
            smallUrl = null,
            thumbnail = null,
            license = null,
            licenseName = null,
            licenseUrl = null
        )

        // Create scientific name list
        val scientificName = if (entity.scientificName.isNotEmpty()) {
            entity.scientificName.split(", ").toList()
        } else {
            emptyList()
        }

        // Create sunlight list
        val sunlight = if (entity.sunlight.isNotEmpty()) {
            entity.sunlight.split(", ").toList()
        } else {
            emptyList()
        }

        return PlantData(
            id = entity.id,
            commonName = entity.commonName,
            scientificName = scientificName,
            family = entity.family,
            genus = entity.genus,
            defaultImage = defaultImage,
            description = entity.description,
            cycle = entity.cycle,
            watering = entity.watering,
            sunlight = sunlight,
            maintenance = entity.maintenance,
            growthRate = entity.growthRate,
            droughtTolerant = entity.droughtTolerant,
            saltTolerant = entity.saltTolerant,
            indoor = entity.indoor,
            careLevel = entity.careLevel,
            flowers = entity.flowers,
            cones = entity.cones,
            fruits = entity.fruits,
            leaf = entity.leaf,
            careGuides = null,
            attracts = null,
            authority = null,
            cuisine = null,
            cultivar = null,
            dimensions = null,
            edibleFruit = null,
            edibleLeaf = null,
            floweringSeason = null,
            hardiness = null,
            hardinessLocation = null,
            harvestSeason = null,
            hybrid = null,
            invasive = null,
            medicinal = null,
            origin = null,
            otherImages = null,
            otherName = null,
            pestSusceptibility = null,
            plantAnatomy = null,
            poisonousToHumans = null,
            poisonousToPets = null,
            propagation = null,
            pruningCount = null,
            pruningMonth = null,
            seeds = null,
            soil = null,
            speciesEpithet = null,
            subspecies = null,
            thorny = null,
            tropical = null,
            type = null,
            variety = null,
            wateringGeneralBenchmark = null,
            xPlantSpacingRequirement = null,
            xSunlightDuration = null,
            xTemperatureTolence = null,
            xWateringAvgVolumeRequirement = null,
            xWateringBasedTemperature = null,
            xWateringDepthRequirement = null,
            xWateringPeriod = null,
            xWateringPhLevel = null,
            xWateringQuality = null
        )
    }
}
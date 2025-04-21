package com.xxu.growguide.api

import android.content.Context
import android.util.Log
import com.xxu.growguide.data.database.AppDatabase
import com.xxu.growguide.data.database.PlantsDao
import com.xxu.growguide.data.entity.PlantsEntity
import com.xxu.growguide.data.entity.UserPlantsEntity
import com.xxu.growguide.data.models.plants.DefaultImage
import com.xxu.growguide.data.models.plants.PlantData
import com.xxu.growguide.data.models.plants.list.PlantsListData
import com.xxu.growguide.ui.viewmodels.UserPlantWithDetails
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

/**
 * Purpose: Manager class that handles fetching and caching plant data
 */
class PlantsManager(
    private val plantsApiService: PlantsApiService,
    private val database: AppDatabase,
    private val context: Context
) {
    private val plantsDao = database.plantsDao()
    private val userPlantsDao = database.userPlantsDao()

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


    suspend fun saveUserPlant(plant: UserPlantsEntity?) {
        try {
            if (plant != null) {
                userPlantsDao.insertUserPlant(plant)
            }
        } catch (e: Exception) {
            throw e
        }
    }

    /**
     * Purpose: Get all plants in a user's garden with their details
     *
     * @param userId The ID of the user whose garden to retrieve
     * @return Flow of UserPlantWithDetails containing user plants with their details
     */
    fun getUserPlants(userId: String): Flow<List<UserPlantWithDetails>> {
        return userPlantsDao.getUserPlants(userId)
            .map { userPlantsList ->
                val userPlantsWithDetails = mutableListOf<UserPlantWithDetails>()

                for (userPlant in userPlantsList) {
                    val plant = plantsDao.getPlantById(userPlant.plantId)
                    if (plant != null) {
                        userPlantsWithDetails.add(
                            UserPlantWithDetails(
                                userPlant = userPlant,
                                plant = plant
                            )
                        )
                    }
                }

                userPlantsWithDetails
            }
    }

    fun getUserPlantDetail(userPlantId: Long): Flow<UserPlantsEntity?> {
        return flow {
            try {
                val userPlant = userPlantsDao.getUserPlantById(userPlantId)
                emit(userPlant)
            } catch (e: Exception) {
                Log.e("PlantsManager", "Error retrieving user plant: ${e.message}")
                emit(null)
            }
        }
    }

    /**
     * Purpose: Remove a plant from user's garden
     *
     * @param userPlantId The ID of the user plant to remove
     * @return True if successful, false otherwise
     */
    suspend fun removeUserPlant(userPlantId: Long): Boolean {
        return try {
            val userPlant = userPlantsDao.getUserPlantById(userPlantId) ?: return false
            userPlantsDao.deleteUserPlant(userPlant)
            true
        } catch (e: Exception) {
            Log.e("PlantsManager", "Error removing plant: ${e.message}")
            false
        }
    }

    /**
     * Purpose: Record watering for a plant in user's garden
     *
     * @param userPlantId The ID of the user plant to update
     * @return True if successful, false otherwise
     */
    suspend fun recordPlantWatering(userPlantId: Long): Boolean {
        return try {
            // Get the plant
            val userPlant = userPlantsDao.getUserPlantById(userPlantId) ?: return false

            // Update the watering date
            val updatedPlant = userPlant.copy(lastWateredDate = System.currentTimeMillis())
            userPlantsDao.updateUserPlant(updatedPlant)

            true
        } catch (e: Exception) {
            Log.e("PlantsManager", "Error recording watering: ${e.message}")
            false
        }
    }


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
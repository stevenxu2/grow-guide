package com.xxu.growguide.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.xxu.growguide.data.entity.PlantsEntity
import kotlinx.coroutines.flow.Flow

/**
 * Purpose: Data Access Object (DAO) for plant-related database operations
 */
@Dao
interface PlantsDao {
    /**
     * Purpose: Insert a plant into the database.
     *          If a plant with the same ID already exists, it will be replaced.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlant(plant: PlantsEntity)

    /**
     * Purpose: Get a plant by its ID
     * @return A specific PlantsEntity by its ID
     */
    @Query("SELECT * FROM plants WHERE id = :id")
    suspend fun getPlantById(id: Int): PlantsEntity?

    /**
     * Purpose: Search plants by name
     * @return The search results for the plant name
     */
    @Query("SELECT * FROM plants WHERE commonName LIKE '%' || :query || '%' OR scientificName LIKE '%' || :query || '%' ORDER BY commonName ASC")
    fun searchPlants(query: String): Flow<List<PlantsEntity>>

    /**
     * Purpose: Check if a plant with the given ID already exists in the database
     * @param id The ID of the plant to check
     * @return True if the plant exists, false otherwise
     */
    @Query("SELECT EXISTS(SELECT 1 FROM plants WHERE id = :id)")
    suspend fun plantExists(id: Int): Boolean

    /**
     * Purpose: Delete all plants data from the database
     */
    @Query("DELETE FROM plants")
    suspend fun deleteAllPlants()
}
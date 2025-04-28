package com.xxu.growguide.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.xxu.growguide.data.entity.UserPlantsEntity
import kotlinx.coroutines.flow.Flow

/**
 * Purpose: Data Access Object (DAO) for user plants database operations
 */
@Dao
interface UserPlantsDao {
    /**
     * Purpose: Insert a user plant into the database
     * @return The row ID of the newly inserted user plant
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserPlant(userPlant: UserPlantsEntity)

    /**
     * Purpose: Get all plants for a specific user
     * @param userId The ID of the user whose plants to retrieve
     * @return A flow of all user plants for the specified user
     */
    @Query("SELECT * FROM user_plants WHERE userId = :userId ORDER BY dateAdded DESC")
    fun getUserPlants(userId: String): Flow<List<UserPlantsEntity>>

    /**
     * Purpose: Get a specific user plant by ID
     * @param userPlantId The ID of the user plant to retrieve
     * @return The user plant with the specified ID
     */
    @Query("SELECT * FROM user_plants WHERE userPlantId = :userPlantId")
    suspend fun getUserPlantById(userPlantId: Long): UserPlantsEntity?

    /**
     * Purpose: Delete a user plant from the database
     */
    @Delete
    suspend fun deleteUserPlant(userPlant: UserPlantsEntity)

    /**
     * Purpose: Update a user plant in the database
     */
    @Update
    suspend fun updateUserPlant(userPlant: UserPlantsEntity)

    /**
     * Purpose: Check if a plant is already in user's garden
     * @param userId The ID of the user
     * @param plantId The ID of the plant
     * @return True if the plant is already in the user's garden, false otherwise
     */
    @Query("SELECT EXISTS(SELECT 1 FROM user_plants WHERE userId = :userId AND plantId = :plantId)")
    suspend fun isPlantInUserGarden(userId: String, plantId: Int): Boolean
}
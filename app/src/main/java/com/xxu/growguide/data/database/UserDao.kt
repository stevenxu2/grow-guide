package com.xxu.growguide.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.xxu.growguide.data.entity.UserEntity

/**
 * Purpose: Data Access Object (DAO) for users database operations
 */
@Dao
interface UserDao {
    /**
     * Purpose: Insert a usr into the database.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    /**
     * Purpose: Get a user by their ID
     * @return A specific UserEntity by their ID
     */
    @Query("SELECT * FROM users WHERE userId = :userId")
    suspend fun getUser(userId: String): UserEntity?


}
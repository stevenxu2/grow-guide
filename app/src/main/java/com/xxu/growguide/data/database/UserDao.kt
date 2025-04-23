package com.xxu.growguide.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
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

    /**
     * Purpose: Update specific user fields
     */
    @Update
    suspend fun updateUser(user: UserEntity)

    /**
     * Purpose: Update user's last active timestamp
     */
    @Query("UPDATE users SET lastActive = :timestamp WHERE userId = :userId")
    suspend fun updateLastActive(userId: String, timestamp: Long)

    /**
     * Purpose: Delete a user from the local database
     */
    @Query("DELETE FROM users WHERE userId = :userId")
    suspend fun deleteUser(userId: String)

}
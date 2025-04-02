package com.xxu.growguide.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
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

}
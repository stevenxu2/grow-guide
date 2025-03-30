package com.xxu.growguide.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.xxu.growguide.data.entity.WeatherEntity

/**
 * Purpose: Data Access Object (DAO) for weather-related database operations
 */
@Dao
interface WeatherDao {
    /**
     * Purpose: Insert weather data into the database
     * If there's a conflict, replace the existing record
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeather(weatherEntity: WeatherEntity)

    /**
     * Purpose: Get the most recent weather data from the database
     * @return The most recent WeatherEntity or null if none exists
     */
    @Query("SELECT * FROM weather ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLatestWeather(): WeatherEntity?

    /**
     * Purpose: Delete all weather data older than a certain timestamp
     * @param timestamp The cutoff timestamp
     */
    @Query("DELETE FROM weather WHERE timestamp < :timestamp")
    suspend fun deleteOldWeatherData(timestamp: Long)

    /**
     * Purpose: Delete all weather data from the database
     */
    @Query("DELETE FROM weather")
    suspend fun deleteAllWeather()
}
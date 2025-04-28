package com.xxu.growguide.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Purpose: Database entity for storing weather information
 */
@Entity(tableName = "weather")
data class WeatherEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    // Location data
    val locationName: String,
    val country: String,
    val region: String,

    // Current weather data
    val tempC: Double,
    val tempF: Double,
    val conditionText: String,
    val humidity: Int,
    val isDay: Int,
    val windKph: Double,
    val windDir: String,
    val precipMm: Double,
    val lastUpdated: String,

    val timestamp: Long  // When this entry was last updated
)
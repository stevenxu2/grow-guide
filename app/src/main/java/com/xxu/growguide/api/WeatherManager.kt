package com.xxu.growguide.api

import android.content.Context
import android.util.Log
import com.xxu.growguide.data.database.WeatherDao
import com.xxu.growguide.data.entity.WeatherEntity
import com.xxu.growguide.data.models.weather.WeatherData
import com.xxu.growguide.data.utils.LocationHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.io.IOException
import java.util.concurrent.TimeUnit

/**
 * Purpose: Manager class that handles fetching and caching weather data
 */
class WeatherManager(
    private val weatherApiService: WeatherApiService,
    private val weatherDao: WeatherDao,
    private val context: Context
) {
    companion object {
        private const val CACHE_DURATION_HOURS = 1
    }

    /**
     * Purpose: Get current weather for the user's location
     * @return Flow of WeatherData from either cache or network
     */
    fun getCurrentWeather(): Flow<WeatherData> = flow {
        var cachedWeather: WeatherEntity? = null
        try {
            // Check cache first
            cachedWeather = weatherDao.getLatestWeather()

            // If cache exists and is fresh, emit cached data
            if (cachedWeather != null && !isCacheOutdated(cachedWeather.timestamp)) {
                emit(mapEntityToWeatherData(cachedWeather))
                return@flow
            }

            // Otherwise, fetch from network
            val location = LocationHelper.getCurrentLocation(context)
            val locationString = LocationHelper.formatLocationForWeatherApi(location)
            val apiKey = API.getWeatherApiKey()

            // Pass Latitude/Longitude as location
            val freshWeather = weatherApiService.getCurrentWeather(
                apiKey = apiKey,
                location = locationString
            )

            // Cache the new data from database
            val weatherEntity = mapWeatherDataToEntity(freshWeather)
            weatherDao.insertWeather(weatherEntity)

            // Emit the fresh data
            emit(freshWeather)

        } catch (e: IOException) {
            // If network request fails but we have cached data, emit the cached data
            if (cachedWeather != null) {
                emit(mapEntityToWeatherData(cachedWeather))
            } else {
                throw e
            }
        } catch (e: Exception) {
            throw e
        }
    }.flowOn(Dispatchers.IO)


    /**
     * Check if the cached data is older than the cache duration
     */
    private fun isCacheOutdated(timestamp: Long): Boolean {
        val currentTime = System.currentTimeMillis()
        val cacheAge = currentTime - timestamp
        val maxCacheAge = TimeUnit.HOURS.toMillis(CACHE_DURATION_HOURS.toLong())
        return cacheAge > maxCacheAge
    }

    /**
     * Map WeatherData model to WeatherEntity for database storage
     */
    private fun mapWeatherDataToEntity(weatherData: WeatherData): WeatherEntity {
        return WeatherEntity(
            id = 0, // Room will auto-generate
            locationName = weatherData.location?.name ?: "",
            country = weatherData.location?.country ?: "",
            region = weatherData.location?.region ?: "",
            tempC = weatherData.current?.tempC ?: 0.0,
            tempF = weatherData.current?.tempF ?: 0.0,
            conditionText = weatherData.current?.condition?.text ?: "",
            humidity = weatherData.current?.humidity ?: 0,
            isDay = weatherData.current?.isDay ?: 1,
            windKph = weatherData.current?.windKph ?: 0.0,
            windDir = weatherData.current?.windDir ?: "",
            precipMm = weatherData.current?.precipMm ?: 0.0,
            lastUpdated = weatherData.current?.lastUpdated ?: "",
            timestamp = System.currentTimeMillis()
        )
    }

    /**
     * Map WeatherEntity from database to WeatherData model
     */
    private fun mapEntityToWeatherData(entity: WeatherEntity): WeatherData {
        val condition = com.xxu.growguide.data.models.weather.Condition(
            text = entity.conditionText,
            icon = null,
            code = null // We don't store this in the DB
        )

        val current = com.xxu.growguide.data.models.weather.Current(
            tempC = entity.tempC,
            tempF = entity.tempF,
            condition = condition,
            humidity = entity.humidity,
            windKph = entity.windKph,
            windDir = entity.windDir,
            precipMm = entity.precipMm,
            isDay = entity.isDay,
            lastUpdated = entity.lastUpdated,
            // Default values for fields we don't cache
            cloud = null,
            dewpointC = null,
            dewpointF = null,
            feelslikeC = null,
            feelslikeF = null,
            gustKph = null,
            gustMph = null,
            heatindexC = null,
            heatindexF = null,
            lastUpdatedEpoch = null,
            precipIn = null,
            pressureIn = null,
            pressureMb = null,
            uv = null,
            visKm = null,
            visMiles = null,
            windDegree = null,
            windMph = null,
            windchillC = null,
            windchillF = null
        )

        val location = com.xxu.growguide.data.models.weather.Location(
            name = entity.locationName,
            country = entity.country,
            region = entity.region,
            // Default values for fields we don't cache
            lat = null,
            lon = null,
            localtime = null,
            localtimeEpoch = null,
            tzId = null
        )

        return WeatherData(
            current = current,
            location = location
        )
    }
}
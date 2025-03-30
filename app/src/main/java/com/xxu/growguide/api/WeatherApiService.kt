package com.xxu.growguide.api

import com.xxu.growguide.data.models.weather.WeatherData
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Purpose: Interface for WeatherAPI.com API calls
 */
interface WeatherApiService {
    /**
     * Purpose: Get current weather data
     * Endpoint: http://api.weatherapi.com/v1/current.json
     */
    @GET("current.json")
    suspend fun getCurrentWeather(
        @Query("key") apiKey: String,
        @Query("q") location: String,
        @Query("aqi") airQualityData: String = "no"
    ): WeatherData
}
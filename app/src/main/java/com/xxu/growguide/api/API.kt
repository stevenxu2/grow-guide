package com.xxu.growguide.api

import android.content.Context
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

/**
 * Provides API related dependencies
 */
object API {

    // Weather API base URL from weatherapi.com
    private const val WEATHER_API_BASE_URL = "http://api.weatherapi.com/v1/"
    private const val WEATHER_API_KEY = "247671e22330467db2842104251003"

    // Plants API base URL from
    private const val PLANTS_API_BASE_URL = "https://your-plants-api.com/api/"
    private const val PLANTS_API_KEY = "000"

    /**
     * Create Moshi instance with Kotlin adapter factory
     */
    private fun provideMoshi(): Moshi {
        return Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
    }

    /**
     * Create Weather API service for WeatherAPI.com
     */
    fun provideWeatherApiService(): WeatherApiService {
        val moshi = provideMoshi()

        return Retrofit.Builder()
            .baseUrl(WEATHER_API_BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(WeatherApiService::class.java)
    }

    /**
     * Create Plants API service
     */
    fun providePlantsApiService(): PlantsApiService {
        val moshi = provideMoshi()

        return Retrofit.Builder()
            .baseUrl(PLANTS_API_BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(PlantsApiService::class.java)
    }

    /**
     * Get the Weather API key safely
     */
    fun getWeatherApiKey(): String {
        return WEATHER_API_KEY
    }
}
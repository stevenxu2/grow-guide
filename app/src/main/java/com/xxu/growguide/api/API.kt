package com.xxu.growguide.api

import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory


/**
 * Purpose: Provides API related dependencies
 */
object API {
    // Weather API base URL from weatherapi.com
    // Use https instead of http for security policy. Otherwise, it will cause an error.
    private const val WEATHER_API_BASE_URL = "https://api.weatherapi.com/v1/"
    private const val WEATHER_API_KEY = "247671e22330467db2842104251003"

    // Plants API base URL from perenual.com
    private const val PLANTS_API_BASE_URL = "https://perenual.com/api/"
    private const val PLANTS_API_KEY = "sk-Ijdi67ea15d3f3c529520"
    //private const val PLANTS_API_KEY = "sk-5GJ867ecd3a02ba609559"

    /**
     * Purpose: Create Moshi instance with Kotlin adapter factory
     */
    private fun provideMoshi(): Moshi {
        return Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
    }

    /**
     * Purpose: Create Weather API service for WeatherAPI.com
     */
    fun provideWeatherApiService(): WeatherApiService {
        val moshi = provideMoshi()

        // Create a logging interceptor
        val loggingInterceptor = HttpLoggingInterceptor { message ->
            Log.d("API_Weather", message)
        }.apply {
            level = HttpLoggingInterceptor.Level.BASIC // Shows URL, method, and headers
        }

        // Create OkHttpClient with the interceptor
        val client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()

        return Retrofit.Builder()
            .baseUrl(WEATHER_API_BASE_URL)
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(WeatherApiService::class.java)
    }

    /**
     * Purpose: Create Plants API service
     */
    fun providePlantsApiService(): PlantsApiService {
        val moshi = provideMoshi()

        // Create a logging interceptor
        val loggingInterceptor = HttpLoggingInterceptor { message ->
            Log.d("API_Plants", message)
        }.apply {
            level = HttpLoggingInterceptor.Level.BASIC // Shows URL, method, and headers
        }

        // Create OkHttpClient with the interceptor
        val client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()

        return Retrofit.Builder()
            .baseUrl(PLANTS_API_BASE_URL)
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(PlantsApiService::class.java)
    }

    /**
     * Purpose: Get the Weather API key safely
     */
    fun getWeatherApiKey(): String {
        return WEATHER_API_KEY
    }

    /**
     * Purpose: Get the Plants API key safely
     */
    fun getPlantsApiKey(): String {
        return PLANTS_API_KEY
    }
}
package com.xxu.growguide.data.utils

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.LocationServices
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

/**
 * Utility object for location-related functions
 */
object LocationHelper {
    private const val TAG = "LocationHelper"
    /**
     * Checks if location permissions are granted
     */
    fun hasLocationPermission(context: Context): Boolean {
        return ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * Gets the current location as a one-time request
     * Returns null if permission is not granted
     */
    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocation(context: Context): Location? = suspendCoroutine { continuation ->
        if (!hasLocationPermission(context)) {
            Log.d(TAG, "Location permission not granted")
            continuation.resume(null)
            return@suspendCoroutine
        } else {
            Log.d(TAG, "Location permission granted")
        }

        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                continuation.resume(location)
            }
            .addOnFailureListener { exception ->
                continuation.resumeWithException(exception)
            }
    }

    /**
     * Formats location for weather API
     */
    fun formatLocationForWeatherApi(location: Location?): String {
        return if (location != null) {
            "${location.latitude},${location.longitude}"
        } else {
            // Default location if none available
            "Winnipeg"
        }
    }
}
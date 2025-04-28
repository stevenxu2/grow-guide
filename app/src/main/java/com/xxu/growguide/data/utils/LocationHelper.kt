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
    // Tag for logging
    private const val TAG = "LocationHelper"

    /**
     * Purpose: Checks if location permissions are granted
     *
     * @param context The context used to check for permissions
     * @return Boolean indicating whether location permissions are granted (true) or not (false)
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
     * Purpose: Gets the current location as a one-time request
     *
     * @param context The context used to access location services
     * @return Location object containing the current location, or null if permission is not granted
     *         or location cannot be determined
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
     * Purpose: Formats location for weather API consumption
     *
     * @param location The location object to format, can be null
     * @return String in the format "latitude,longitude" if location is available, or default location "Winnipeg" if null
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
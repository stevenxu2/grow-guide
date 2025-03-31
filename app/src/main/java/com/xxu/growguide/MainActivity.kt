package com.xxu.growguide

import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.xxu.growguide.api.API
import com.xxu.growguide.api.WeatherManager
import com.xxu.growguide.data.database.AppDatabase
import com.xxu.growguide.destinations.Destination
import com.xxu.growguide.navigation.BottomNav
import com.xxu.growguide.ui.components.FloatingButton
import com.xxu.growguide.ui.screens.CommunityScreen
import com.xxu.growguide.ui.screens.HomeScreen
import com.xxu.growguide.ui.screens.OnboardingScreen
import com.xxu.growguide.ui.screens.PlantsScreen
import com.xxu.growguide.ui.screens.ProfileScreen
import com.xxu.growguide.ui.theme.GrowGuideTheme
import com.xxu.growguide.viewmodels.ThemeViewModel
import com.xxu.growguide.viewmodels.WeatherViewModel
import com.xxu.growguide.viewmodels.WeatherViewModelFactory
import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.content.ContextCompat

class MainActivity : ComponentActivity() {
    // Define the permission request handler at the class level
    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                Log.d("LocationPermission", "Precise location access granted")
            }
            permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                Log.d("LocationPermission", "Approximate location access granted")
            }
            else -> {
                Log.d("LocationPermission", "No location access granted")
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize the database
        val database = AppDatabase.getInstance(applicationContext)

        // Initialize the API services
        val weatherApiService = API.provideWeatherApiService()

        // Initialize the managers
        val weatherManager = WeatherManager(
            weatherApiService,
            database.weatherDao(),
            applicationContext
        )

        // Create the ViewModels
        val weatherViewModel = ViewModelProvider(
            this,
            WeatherViewModelFactory(weatherManager)
        )[WeatherViewModel::class.java]

        setContent {
            val themeViewModel: ThemeViewModel = viewModel()

            GrowGuideTheme(themeViewModel = themeViewModel) {
                //val sharedPrefs = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                //val onboardingComplete = sharedPrefs.getBoolean("onboarding_complete", false)
                //var showOnboarding by remember { mutableStateOf(!onboardingComplete) }
                var showOnboarding by remember { mutableStateOf(true) }

                if (showOnboarding) {
                    // Onboarding Screen
                    OnboardingScreen(
                        onFinishOnboarding = {
                            // Save that onboarding is complete
                            //sharedPrefs.edit().putBoolean("onboarding_complete", true).apply()
                            showOnboarding = false
                        }
                    )
                } else {
                    // Main App
                    val navController = rememberNavController()
                    App(
                        navController = navController,
                        themeViewModel = themeViewModel,
                        weatherViewModel = weatherViewModel
                    )
                }
            }
        }

        // Check if we need to request permissions
        if (!hasLocationPermissions()) {
            // Request permissions if not already granted
            requestLocationPermissions()
        } else {
            Log.d("LocationPermission", "Permissions already granted")
            // Proceed with your app initialization that requires location
        }
    }

    private fun hasLocationPermissions(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestLocationPermissions() {
        Log.d("LocationPermission", "Requesting location permissions")
        locationPermissionRequest.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }
}

/**
 * App entry
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    themeViewModel: ThemeViewModel,
    weatherViewModel: WeatherViewModel
){

    val scrollState = rememberScrollState()
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        bottomBar = { BottomNav(navController = navController) },
        floatingActionButton = {
            if (currentRoute == Destination.Home.route) {
                FloatingButton("Add a new Plant") {
                    // add a new plant
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController as NavHostController, startDestination = Destination.Home.route
        ) {
            composable(Destination.Home.route) {
                HomeScreen(
                    navController = navController,
                    innerPadding,
                    scrollState,
                    weatherViewModel
                )
            }
            composable(Destination.Plants.route) {
                PlantsScreen(
                    navController = navController,
                    innerPadding,
                    scrollState)
            }
            composable(Destination.Community.route) {
                CommunityScreen(
                    navController = navController,
                    innerPadding,
                    scrollState)
            }
            composable(Destination.Profile.route) {
                ProfileScreen(
                    navController = navController,
                    innerPadding,
                    scrollState,
                    themeViewModel
                )
            }
        }
    }
}



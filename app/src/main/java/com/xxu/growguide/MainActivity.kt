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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.core.content.ContextCompat
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.xxu.growguide.api.PlantManager
import com.xxu.growguide.auth.AuthManager
import com.xxu.growguide.ui.screens.AddPlantScreen
import com.xxu.growguide.ui.screens.LoginScreen
import com.xxu.growguide.ui.screens.PlantDetailScreen
import com.xxu.growguide.ui.viewmodels.PlantDetailViewModel
import com.xxu.growguide.ui.viewmodels.PlantDetailViewModelFactory
import com.xxu.growguide.ui.viewmodels.PlantsViewModel
import com.xxu.growguide.ui.viewmodels.PlantsViewModelFactory
import com.xxu.growguide.viewmodels.AuthViewModel
import kotlinx.coroutines.launch

/**
 * Purpose: Main entry point for the application
 *
 * Initializes app components
 */
class MainActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var authManager: AuthManager
    private lateinit var authViewModel: AuthViewModel

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

        // Initialize Firebase Auth
        auth = Firebase.auth

        // Initialize the AuthManager
        authManager = AuthManager.getInstance(applicationContext)

        // Initialize the AuthViewModel
        authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]

        // Initialize the database
        val database = AppDatabase.getInstance(applicationContext)

        // Create weather view model
        val weatherApiService = API.provideWeatherApiService()
        val weatherManager = WeatherManager(
            weatherApiService,
            database.weatherDao(),
            applicationContext
        )
        val weatherViewModel = ViewModelProvider(
            this,
            WeatherViewModelFactory(weatherManager)
        )[WeatherViewModel::class.java]

        // Create weather view model
        val plantServiceApi = API.providePlantsApiService()
        val plantManager = PlantManager(
            plantServiceApi,
            database.plantsDao(),
            applicationContext
        )
        val plantsViewModel = ViewModelProvider(
            this,
            PlantsViewModelFactory(
                plantManager,
                database.plantsDao()
            )
        )[PlantsViewModel::class.java]
        val plantDetailViewModel = ViewModelProvider(
            this,
            PlantDetailViewModelFactory(
                plantManager,
                database.plantsDao()
            )
        )[PlantDetailViewModel::class.java]


        setContent {
            val themeViewModel: ThemeViewModel = viewModel()

            GrowGuideTheme(themeViewModel = themeViewModel) {
                //val sharedPrefs = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                //val onboardingComplete = sharedPrefs.getBoolean("onboarding_complete", false)
                //var showOnboarding by remember { mutableStateOf(!onboardingComplete) }
                var showOnboarding by remember { mutableStateOf(true) }

                // show the login dialog if necessary
                val showLoginDialog by authViewModel.showLoginDialog.collectAsState()

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
                        weatherViewModel = weatherViewModel,
                        plantsViewModel = plantsViewModel,
                        plantDetailViewModel = plantDetailViewModel,
                        authViewModel = authViewModel
                    )

                    // Show login dialog when needed
                    if (showLoginDialog) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(MaterialTheme.colorScheme.background.copy(alpha = 0.9f))
                        ) {
                            LoginScreen(
                                navController = navController,
                                authManager = authViewModel.getAuthManager(),
                                onDismiss = { authViewModel.hideLogin() }
                            )
                        }
                    }
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

//    public override fun onStart() {
//        super.onStart()
//        // Check if user is signed in (non-null) and update UI accordingly.
//        val currentUser = auth.currentUser
//        if (currentUser != null) {
//            TODO()
//        }
//    }

    /**
     * Purpose: Check if location permissions are already granted
     *
     * @return Boolean indicating whether fine or coarse location permission is granted
     */
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

    /**
     * Purpose: Request location permissions from the user
     *
     * Launches the permission request dialog for both fine and coarse location
     */
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
 * Purpose: Main app composable that sets up navigation and screen structure
 *
 * Creates the app scaffold with bottom navigation bar, floating action button,
 * and navigation host for different screens.
 *
 * @param modifier Modifier for customizing the layout
 * @param navController Navigation controller for handling screen navigation
 * @param themeViewModel ViewModel that manages theme preferences
 * @param weatherViewModel ViewModel that provides weather data
 * @param plantsViewModel ViewModel that manages plants list data
 * @param plantDetailViewModel ViewModel that manages plant detail data
 * @param authViewModel ViewModel that handles authentication state
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    themeViewModel: ThemeViewModel,
    weatherViewModel: WeatherViewModel,
    plantsViewModel: PlantsViewModel,
    plantDetailViewModel: PlantDetailViewModel,
    authViewModel: AuthViewModel
){
    // only for development
    LaunchedEffect(key1 = Unit) {
        val isSignIn = authViewModel.getAuthManager().signIn("xqxu512@gmail.com", "gg123456")
        Log.i("Auth", "${isSignIn}")
    }


    val scrollState = rememberScrollState()
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
    val isLoggedIn by remember { authViewModel.isLoggedIn }

    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        bottomBar = { BottomNav(navController = navController) },
        floatingActionButton = {
            if (currentRoute == Destination.Home.route) {
                FloatingButton("Add a new Plant") {
                    // Check if logged in before adding a plant
                    if (isLoggedIn) {
                        navController.navigate(Destination.AddPlant.route)
                    } else {
                        authViewModel.showLogin()
                    }
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
                    scrollState,
                    plantsViewModel
                )
            }
            composable(Destination.Community.route) {
                CommunityScreen(
                    navController = navController,
                    innerPadding,
                    scrollState
                )
            }
            composable(Destination.Profile.route) {
                ProfileScreen(
                    navController = navController,
                    innerPadding,
                    scrollState,
                    themeViewModel
                )
            }
            composable(Destination.AddPlant.route) {
                AddPlantScreen(
                    navController = navController,
                    innerPadding = innerPadding
                )
            }
            composable(
                route = Destination.PlantDetail.routeWithArgs,
                arguments = listOf(
                    navArgument(Destination.PlantDetail.plantIdArg) {
                        type = NavType.IntType
                    }
                )
            ) { backStackEntry ->
                val plantId = backStackEntry.arguments?.getInt(Destination.PlantDetail.plantIdArg) ?: 0
                PlantDetailScreen(
                    navController = navController,
                    innerPadding = innerPadding,
                    plantId = plantId,
                    viewModel = plantDetailViewModel
                )
            }
        }
    }
}



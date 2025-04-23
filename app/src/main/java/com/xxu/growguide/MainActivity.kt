package com.xxu.growguide

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
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
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.core.content.ContextCompat
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.xxu.growguide.api.PlantsManager
import com.xxu.growguide.auth.AuthManager
import com.xxu.growguide.ui.screens.AddPlantScreen
import com.xxu.growguide.ui.screens.GardenPlantDetailScreen
import com.xxu.growguide.ui.screens.GardenScreen
import com.xxu.growguide.ui.screens.LoginScreen
import com.xxu.growguide.ui.screens.MockPlantsScreen
import com.xxu.growguide.ui.screens.PlantDetailScreen
import com.xxu.growguide.ui.viewmodels.MockPlantsViewModel
import com.xxu.growguide.ui.viewmodels.MockPlantsViewModelFactory
import com.xxu.growguide.ui.viewmodels.PlantDetailViewModel
import com.xxu.growguide.ui.viewmodels.PlantDetailViewModelFactory
import com.xxu.growguide.viewmodels.AuthViewModel
import com.xxu.growguide.viewmodels.AuthViewModelFactory

/**
 * Purpose: Main entry point for the application
 *
 * Initializes app components
 */
class MainActivity : ComponentActivity() {
    companion object {
        private const val PREFS_NAME = "GrowGuidePrefs"
        private const val KEY_ONBOARDING_COMPLETE = "onboardingComplete"
    }
    // Reference to SharedPreferences
    private lateinit var sharedPreferences: SharedPreferences

    private lateinit var auth: FirebaseAuth
    private lateinit var authManager: AuthManager
    private lateinit var authViewModel: AuthViewModel

    private lateinit var plantsManager: PlantsManager
    private lateinit var database: AppDatabase

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

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)

        // Initialize Firebase Auth
        auth = Firebase.auth

        // Initialize the AuthManager
        authManager = AuthManager.getInstance(applicationContext)

        // Initialize the AuthViewModel
        authViewModel = ViewModelProvider(
            this,
            AuthViewModelFactory(authManager)
        )[AuthViewModel::class.java]

        // Initialize the database
        database = AppDatabase.getInstance(applicationContext)

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

        // Create plants view model
        val plantServiceApi = API.providePlantsApiService()

        // Create the plants manager
        plantsManager = PlantsManager(
            plantServiceApi,
            database,
            applicationContext
        )


        setContent {
            val themeViewModel: ThemeViewModel = viewModel()

            GrowGuideTheme(themeViewModel = themeViewModel) {
                val onboardingComplete = sharedPreferences.getBoolean(KEY_ONBOARDING_COMPLETE, false)

                // Retrieve the flag (default to true if not found)
                var showOnboarding by remember { mutableStateOf(!onboardingComplete) }

                // show the login dialog if necessary
                //val showLoginDialog by authViewModel.showLoginDialog.collectAsState()

                if (showOnboarding) {
                    // Onboarding Screen
                    OnboardingScreen(
                        onFinishOnboarding = {
                            // Save that onboarding is complete
                            sharedPreferences.edit().putBoolean(KEY_ONBOARDING_COMPLETE, true).apply()
                            showOnboarding = false
                        }
                    )
                } else {
                    // Main App
                    val navController = rememberNavController()

                    App(
                        navController = navController,
                        database = database,
                        authManager = authManager,
                        plantsManager = plantsManager,
                        themeViewModel = themeViewModel,
                        weatherViewModel = weatherViewModel,
                        authViewModel = authViewModel
                    )

                    // Show Login dialog when needed
//                    if (showLoginDialog) {
//                        Box(
//                            modifier = Modifier
//                                .fillMaxSize()
//                                .background(MaterialTheme.colorScheme.background.copy(alpha = 0.9f))
//                        ) {
//                            LoginScreen(
//                                navController = navController,
//                                authManager = authManager,
//                                onDismiss = { authViewModel.hideLogin() }
//                            )
//                        }
//                    }
                }
            }
        }

        // Check if we need to request permissions
        if (!hasLocationPermissions()) {
            // Request permissions if not already granted
            requestLocationPermissions()
        } else {
            Log.d("LocationPermission", "Permissions already granted")
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
 * @param database Database for the whole app
 * @param themeViewModel ViewModel that manages theme preferences
 * @param weatherViewModel ViewModel that provides weather data
 * @param plantsManager Manager class that handles fetching and caching plant data
 * @param plantDetailViewModel ViewModel that manages plant detail data
 * @param authViewModel ViewModel that handles authentication state
 */
@Composable
fun App(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    database: AppDatabase,
    plantsManager: PlantsManager,
    authManager: AuthManager,
    themeViewModel: ThemeViewModel,
    weatherViewModel: WeatherViewModel,
    authViewModel: AuthViewModel
){
    Log.i("PlantMainActivity", "Rendering MainActivity")

    // only for development, remove it before publishing
//    LaunchedEffect(key1 = Unit) {
//        val isSignIn = authViewModel.getAuthManager().signIn("xqxu512@gmail.com", "gg123456")
//        Log.i("Auth", "${isSignIn}")
//    }

    val scrollState = rememberScrollState()

    // Observe current route
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    // Observe login dialog state
    val showLoginDialog by authViewModel.showLoginDialog.collectAsState()

    // Observe auth state
    val isLoggedIn by authViewModel.isLoggedIn

    // Check if we're on a detail screen that should hide navigation
    val isHiddenNav = currentRoute?.startsWith("plant_detail") == true ||
            currentRoute?.startsWith("add_plant") == true ||
            currentRoute?.startsWith("garden_plant_detail") == true

    // Check if current screen requires authentication
    val requiresAuth = when {
        currentRoute == Destination.Home.route -> true
        currentRoute == Destination.Profile.route -> true
        currentRoute == Destination.Garden.route -> true
        currentRoute?.startsWith(Destination.GardenPlantDetail.route) == true -> true
        currentRoute?.startsWith(Destination.AddPlant.route) == true -> true
        else -> false
    }
    Log.i("PlantMainActivity", "requiresAuth: $requiresAuth")
    Log.i("PlantMainActivity", "isLoggedIn: $isLoggedIn")

    // If screen requires auth but user is not logged in, show login dialog
    LaunchedEffect(requiresAuth, isLoggedIn) {
        if (requiresAuth && !isLoggedIn) {
            Log.i("PlantMainActivity", "showLogin()")
            authViewModel.showLogin()
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize(),

        // Hide the navigation bar for some screens
        bottomBar = {
            // Only show bottom nav on main screens
//            val showBottomNav = when (currentRoute) {
//                Destination.Home.route,
//                Destination.Plants.route,
//                Destination.Community.route,
//                Destination.Profile.route,
//                Destination.Garden.route -> true
//                else -> false
//            }

            if (!isHiddenNav) {
                BottomNav(navController = navController)
            }
        },
        floatingActionButton = {
            if (currentRoute == Destination.Home.route && isLoggedIn) {
                FloatingButton(
                    modifier = Modifier,
                    description = "Add a new Plant"
                ) {
                    navController.navigate(Destination.Plants.route)
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            // Main navigation content
            NavHost(
                navController = navController,
                startDestination = Destination.Home.route
            ) {
                // Home screen
                composable(Destination.Home.route) {
                    HomeScreen(
                        navController = navController,
                        innerPadding = innerPadding,
                        scrollState = scrollState,
                        weatherViewModel = weatherViewModel,
                        plantsManager = plantsManager
                    )
                }
                // Plant screen
                composable(Destination.Plants.route) {
                    PlantsScreen(
                        navController = navController,
                        innerPadding = innerPadding,
                        scrollState = scrollState,
                        plantsManager = plantsManager,
                        database = database
                    )
//                    MockPlantsScreen(
//                        navController = navController,
//                        innerPadding = innerPadding,
//                        scrollState = scrollState
//                    )
                }
                // Garden screen
                composable(Destination.Garden.route) {
                    GardenScreen(
                        navController = navController,
                        innerPadding = innerPadding,
                        plantsManager = plantsManager
                    )
                }
                // Community screen
                composable(Destination.Community.route) {
                    CommunityScreen(
                        navController = navController,
                        innerPadding,
                        scrollState
                    )
                }
                // Profile screen
                composable(Destination.Profile.route) {
                    ProfileScreen(
                        navController = navController,
                        innerPadding,
                        scrollState,
                        themeViewModel
                    )
                }
                // Add Plant screen
                composable(
                    route = Destination.AddPlant.routeWithArgs,
                    arguments = listOf(
                        navArgument(Destination.AddPlant.plantIdArg) {
                            type = NavType.IntType
                        }
                    )
                ) { backStackEntry ->
                    val plantId = backStackEntry.arguments?.getInt(Destination.AddPlant.plantIdArg) ?: 0
                    AddPlantScreen(
                        navController = navController,
                        plantId = plantId,
                        plantsManager = plantsManager
                    )
                }
                // Plant Detail screen
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
                        plantId = plantId,
                        navController = navController,
                        innerPadding = innerPadding,
                        plantsManager = plantsManager,
                    )
                }
                // Garden Plant screen
                composable(
                    route = Destination.GardenPlantDetail.routeWithArgs,
                    arguments = listOf(
                        navArgument(Destination.GardenPlantDetail.userPlantIdArg) {
                            type = NavType.LongType
                        }
                    )
                ) { backStackEntry ->
                    val userPlantId = backStackEntry.arguments?.getLong(Destination.GardenPlantDetail.userPlantIdArg) ?: 0
                    GardenPlantDetailScreen(
                        userPlantId = userPlantId,
                        navController = navController,
                        innerPadding = innerPadding,
                        plantsManager = plantsManager,
                    )
                }
            }

            // Show login dialog when needed
            AnimatedVisibility(visible = showLoginDialog) {
                LoginScreen(
                    navController = navController,
                    authManager = authManager,
                    onDismiss = {
                        authViewModel.hideLogin()
                        if (requiresAuth && !isLoggedIn) {
                            // If user cancels login but screen requires auth,
                            // navigate back to Home
                            navController.navigate(Destination.Home.route) {
                                popUpTo(Destination.Home.route) { inclusive = true }
                            }
                        }
                    }
                )
            }
        }
    }
}



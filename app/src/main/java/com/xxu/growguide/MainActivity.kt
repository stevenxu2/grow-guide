package com.xxu.growguide

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
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

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
                        themeViewModel = themeViewModel
                    )
                }
            }
        }
    }
}

/**
 * App entry
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App(modifier: Modifier = Modifier, navController: NavHostController, themeViewModel: ThemeViewModel){

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
                HomeScreen(navController = navController, innerPadding, scrollState)
            }
            composable(Destination.Plants.route) {
                PlantsScreen(navController = navController, innerPadding, scrollState)
            }
            composable(Destination.Community.route) {
                CommunityScreen(navController = navController, innerPadding, scrollState)
            }
            composable(Destination.Profile.route) {
                ProfileScreen(navController = navController, innerPadding, scrollState, themeViewModel)
            }
        }
    }
}



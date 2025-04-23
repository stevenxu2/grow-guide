package com.xxu.growguide.ui.screens

import android.text.format.DateUtils
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.xxu.growguide.R
import com.xxu.growguide.api.PlantsManager
import com.xxu.growguide.auth.AuthManager
import com.xxu.growguide.destinations.Destination
import com.xxu.growguide.ui.components.TaskType
import com.xxu.growguide.ui.components.TodayTasks
import com.xxu.growguide.ui.components.WeatherCard
import com.xxu.growguide.ui.viewmodels.GardenViewModel
import com.xxu.growguide.ui.viewmodels.GardenViewModelFactory
import com.xxu.growguide.ui.viewmodels.UserPlantWithDetails
import com.xxu.growguide.viewmodels.AuthViewModel
import com.xxu.growguide.viewmodels.AuthViewModelFactory
import com.xxu.growguide.viewmodels.WeatherViewModel

/**
 * Purpose: Displays the main home screen with user's garden information
 *
 * @param navController Navigation controller for screen navigation
 * @param innerPadding Padding values from the parent layout
 * @param scrollState State object for handling scrolling behavior
 * @param weatherViewModel ViewModel that provides weather data and controls
 */
@Composable
fun HomeScreen(
    navController: NavHostController,
    innerPadding: PaddingValues,
    scrollState: ScrollState,
    weatherViewModel: WeatherViewModel,
    plantsManager: PlantsManager
) {
    val authManager: AuthManager = AuthManager.getInstance(LocalContext.current.applicationContext)
    val authViewModel: AuthViewModel = viewModel(factory = AuthViewModelFactory(authManager))

    val gardenViewModel: GardenViewModel = viewModel(factory = GardenViewModelFactory(plantsManager))

    // Get current user ID
    val currentUser = authManager.currentUser.value
    val userId = currentUser?.uid ?: return

    // Collect states from ViewModel
    val gardenPlants by gardenViewModel.gardenPlants.collectAsState()
    val gardenIsLoading by gardenViewModel.isLoading.collectAsState()
    val gardenError by gardenViewModel.error.collectAsState()

    // Collect weather state from ViewModel
    val weatherState by weatherViewModel.weatherState.collectAsState()


    // Fetch weather data when the screen is first displayed
    LaunchedEffect(Unit) {
        weatherViewModel.fetchCurrentWeather()
    }

    // Load garden plants
    LaunchedEffect(userId) {
        if (userId.isNotEmpty()) {
            gardenViewModel.getGardenPlants(userId)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(innerPadding)
            .padding(horizontal = 20.dp, vertical = 16.dp)
            .verticalScroll(scrollState)
    ) {

        HomeHeader(
            displayName = currentUser.displayName.toString()
        )

        WeatherCard(weatherViewModel, weatherState)

        TodayTasks(
            gardenPlants = gardenPlants,
            onTaskClick = { userPlantId ->
                navController.navigate("garden_plant_detail/$userPlantId")
            },
            onCheckTask = { userPlantId, taskType ->
                // Handle task completion
                if (taskType == TaskType.WATER_URGENT ||
                    taskType == TaskType.WATER_SOON ||
                    taskType == TaskType.WATER_ROUTINE ||
                    taskType == TaskType.WATER_NEW_PLANT) {
                    gardenViewModel.recordWatering(userPlantId)
                }
            },
            onSeeAllClick = {
                navController.navigate(Destination.Garden.route)
            }
        )

        MyGarden(
            gardenPlants = gardenPlants,
            gardenIsLoading = gardenIsLoading,
            gardenError = gardenError,
            onClickAdd = { navController.navigate(Destination.Plants.route) },
            onClickView = { userPlantId ->
                navController.navigate("garden_plant_detail/$userPlantId") },
            onClickSeeAll = { navController.navigate(Destination.Garden.route) }
        )

        //CommunityUpdates()
    }
}

/**
 * Purpose: Displays the header section of the home screen with the "My Garden" title
 */
@Composable
fun HomeHeader(
    displayName: String
){
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
    ) {
        // Header
        Text(
            text = "Welcome back,",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            fontSize = 32.sp,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = displayName,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            fontSize = 26.sp,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
    Spacer(modifier = Modifier.height(16.dp))
}

/**
 * Purpose: Displays a section showing the user's tasks for today and upcoming days
 */
@Composable
fun TodayTasks2(){
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerLowest)
            .border(1.dp, MaterialTheme.colorScheme.surfaceContainerLow, RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Today's Tasks",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "See all",
                style = MaterialTheme.typography.labelSmall,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        TaskCard("Water Tomatoes", "Due today")
        TaskCard("Water Strawberries", "Due tomorrow")

    }
    Spacer(modifier = Modifier.height(24.dp))
}

/**
 * Purpose: Displays a single task card with checkbox, title, and due date
 *
 * @param title The title or description of the task
 * @param dueText Text indicating when the task is due
 */
@Composable
fun TaskCard(title: String, dueText: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.width(36.dp)
        ) {
            Checkbox(
                checked = true,
                onCheckedChange = null,
                colors = CheckboxDefaults.colors(
                    checkmarkColor = MaterialTheme.colorScheme.primary,
                    uncheckedColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    checkedColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                fontSize = 14.sp,
            )
        }
    }
    Spacer(modifier = Modifier.height(8.dp))
}

/**
 * Purpose: Displays a section showing the user's plants with their status
 */
@Composable
fun MyGarden(
    gardenPlants: List<UserPlantWithDetails?>,
    gardenIsLoading: Boolean,
    gardenError: String?,
    onClickAdd: () -> Unit,
    onClickView: (Long) -> Unit,
    onClickSeeAll: () -> Unit,
){
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerLowest)
            .border(1.dp, MaterialTheme.colorScheme.surfaceContainerLow, RoundedCornerShape(12.dp))
            .padding(16.dp),
    ) {
        // Headline
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "My Garden",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            if (gardenPlants.size > 3) {
                TextButton (
                    onClick = { onClickSeeAll() }
                ) {
                    Text(
                        text = "See all",
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }

        HorizontalDivider(
            modifier = Modifier.padding(vertical = 8.dp),
            1.dp,
            MaterialTheme.colorScheme.surfaceContainerLow
        )

        // Loading state
        if (gardenIsLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        }
        // Error state
        else if (gardenError != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Error: $gardenError",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
            }
        }
        // Empty state
        else if (gardenPlants.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Button(
                        modifier = Modifier.size(width = 250.dp, height = 50.dp),
                        onClick = { onClickAdd() },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add plants",
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Browse Plants",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Text(
                        text = "Start adding plants to your garden",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }
        // Content state - plant list
        else {
            // Display a limited number of plants
            gardenPlants.take(3).forEach { userPlantWithDetails ->
                GardenPlant(
                    userPlantWithDetails = userPlantWithDetails,
                    onItemClick = { userPlantWithDetails?.userPlant?.userPlantId?.let {
                        onClickView(it)
                    } }
                )
            }
        }
    }
    Spacer(modifier = Modifier.height(24.dp))
}

/**
 * Purpose: A item in a list of the my plants
 */
@Composable
private fun GardenPlant(
    userPlantWithDetails: UserPlantWithDetails?,
    onItemClick: () -> Unit,

) {
    val context = LocalContext.current
    val userPlant = userPlantWithDetails?.userPlant

    // Format last watered date with "time ago" format
    val lastWateredText = userPlant?.lastWateredDate?.let { timestamp ->
        val timeAgo = DateUtils.getRelativeTimeSpanString(
            timestamp,
            System.currentTimeMillis(),
            DateUtils.MINUTE_IN_MILLIS
        )
        "Watered $timeAgo"
    } ?: "Not watered yet"

    Spacer(modifier = Modifier.height(8.dp))

    Row(
        modifier = Modifier
            .clickable { onItemClick() }
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, MaterialTheme.colorScheme.surfaceContainerLow, RoundedCornerShape(16.dp)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Plant image
        Box(
            modifier = Modifier
                .size(100.dp)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            if (!userPlant?.imageUri.isNullOrEmpty()) {
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(userPlant?.imageUri)
                        .crossfade(true)
                        .build(),
                    contentDescription = "User's plant image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_plant_placeholder),
                        contentDescription = "Plant",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(30.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(
            modifier = Modifier
                .weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            Column {
                Text(
                    text = userPlantWithDetails?.userPlant?.nickname.toString(),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    lineHeight = 24.sp
                )
                // Watering info
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.WaterDrop,
                        contentDescription = "Last watered",
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = lastWateredText,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .size(30.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Default.KeyboardArrowRight,
                    contentDescription = "arrow",
                    tint = MaterialTheme.colorScheme.outlineVariant,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

/**
 * Purpose: Displays a section showing recent community posts and updates
 */
@Composable
fun CommunityUpdates(){
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerLowest)
            .border(1.dp, MaterialTheme.colorScheme.surfaceContainerLow, RoundedCornerShape(12.dp))
            .padding(16.dp),
    ) {
        // Headline
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Recent Post",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "See all",
                style = MaterialTheme.typography.labelSmall,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Post list
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceContainerLowest)
        ) {
            PostItem("Get started with house plants")
            PostItem("Houseplant Care Tips")
            PostItem("15 Brilliant Plant Care Tips")
        }
    }
    Spacer(modifier = Modifier.height(24.dp))
}

/**
 * Purpose: Displays a single community post with user avatar and title
 *
 * @param title The title of the community post
 */
@Composable
fun PostItem(title: String) {
    HorizontalDivider(
        modifier = Modifier.padding(vertical = 10.dp),
        color = MaterialTheme.colorScheme.surfaceContainerLow
    )

    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.width(36.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.tertiaryContainer)
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Profile",
                    tint = MaterialTheme.colorScheme.onTertiaryContainer,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column {
                Text(
                    text = title,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    lineHeight = 14.sp,
                    maxLines = 1,
                )
                Text(
                    text = "3 days ago",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.outline,
                    lineHeight = 12.sp,
                )
            }
        }
    }
}
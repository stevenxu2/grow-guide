package com.xxu.growguide.ui.screens

import android.text.format.DateUtils
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.xxu.growguide.R
import com.xxu.growguide.api.PlantsManager
import com.xxu.growguide.auth.AuthManager
import com.xxu.growguide.destinations.Destination
import com.xxu.growguide.ui.components.FloatingButton
import com.xxu.growguide.ui.theme.Sunny
import com.xxu.growguide.ui.viewmodels.GardenViewModel
import com.xxu.growguide.ui.viewmodels.GardenViewModelFactory
import com.xxu.growguide.ui.viewmodels.UserPlantWithDetails
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Purpose: Displays a list of plants in the user's garden
 *
 * @param navController Navigation controller for screen transitions
 * @param innerPadding Padding values passed from the parent layout
 */
@Composable
fun GardenScreen(
    navController: NavController,
    innerPadding: PaddingValues,
    plantsManager: PlantsManager
) {
    val authManager: AuthManager = AuthManager.getInstance(LocalContext.current.applicationContext)
    val viewModel: GardenViewModel = viewModel(factory = GardenViewModelFactory(plantsManager))

    // Get current user ID
    val currentUser = authManager.currentUser.value
    val userId = currentUser?.uid ?: return


    // Load garden plants
    LaunchedEffect(userId) {
        if (userId.isNotEmpty()) {
            Log.i("GardenScreen", "getGardenPlants")
            viewModel.getGardenPlants(userId)
        }
    }

    // Collect states from ViewModel
    val gardenPlants by viewModel.gardenPlants.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    // Delete confirmation dialog state
    var showWaterDialog by remember { mutableStateOf(false) }
    var plantToWater by remember { mutableStateOf<Long?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(innerPadding)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "My Garden",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )

                val plantCount = gardenPlants.size
                val plantCountText = if (plantCount == 1) "1 Plant" else "$plantCount Plants"

                Text(
                    text = plantCountText,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Loading state
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            }
            // Error state
            else if (error != null) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Error: $error",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center
                    )
                }
            }
            // Empty state
            else if (gardenPlants.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_plant_placeholder),
                            contentDescription = "Empty Garden",
                            modifier = Modifier.size(100.dp),
                            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f))
                        )

                        Text(
                            text = "Your garden is empty",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Text(
                            text = "Start adding plants to your garden",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                            textAlign = TextAlign.Center,
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        Button(
                            modifier = Modifier.size(width = 250.dp, height = 50.dp),
                            onClick = { navController.navigate(Destination.Plants.route) },
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
                    }
                }
            }
            // Content state - plant list
            else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(gardenPlants) { userPlantWithDetails ->
                        GardenPlantCard(
                            userPlantWithDetails = userPlantWithDetails,
                            onItemClick = { userPlantId ->
                                navController.navigate("garden_plant_detail/$userPlantId")
                            },
                            onWaterClick = { userPlantId ->
                                showWaterDialog = true
                                plantToWater = userPlantId
                            }
                        )
                    }

                    // Add some bottom spacing for display the floating add button
                    item {
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            }
        }

        if (gardenPlants.isNotEmpty()) {
            // Floating action button to add new plants
            FloatingButton(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp),
                description = "Add a new Plant",
            ) {
                navController.navigate(Destination.Plants.route)
            }
        }

        // Delete confirmation dialog
        if (showWaterDialog && plantToWater != null) {
            AlertDialog(
                onDismissRequest = {
                    showWaterDialog = false
                    plantToWater = null
                },
                title = { Text("Water Plant") },
                text = { Text("Record that you've watered this plant today?") },
                confirmButton = {
                    Button(
                        onClick = {
                            plantToWater?.let { userPlantId ->
                                viewModel.recordWatering(userPlantId)
                            }
                            showWaterDialog = false
                            plantToWater = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text("Yes, I watered it")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            showWaterDialog = false
                            plantToWater = null
                        }
                    ) {
                        Text("Cancel", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            )
        }
    }
}

/**
 * Purpose: Displays a card containing a plant in the user's garden with its details
 *
 * @param userPlantWithDetails Contains the user plant entity and its related plant details
 * @param onItemClick Callback for when the card is clicked
 * @param onWaterClick Callback for when the water button is clicked
 */
@Composable
fun GardenPlantCard(
    userPlantWithDetails: UserPlantWithDetails?,
    onItemClick: (Long) -> Unit,
    onWaterClick: (Long) -> Unit
) {
    val context = LocalContext.current
    val userPlant = userPlantWithDetails?.userPlant
    val plant = userPlantWithDetails?.plant

    // Format planting date
    val dateFormatter = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
    val plantedDate = dateFormatter.format(Date(userPlant!!.plantingDate))

    // Calculate days since planting
    val daysSincePlanting = ((System.currentTimeMillis() - userPlant.plantingDate) / (1000 * 60 * 60 * 24)).toInt()
    val plantedAgo = when {
        daysSincePlanting == 0 -> "Planted today"
        daysSincePlanting == 1 -> "Planted yesterday"
        else -> "Planted $daysSincePlanting days ago"
    }

    // Format last watered date with "time ago" format
    val lastWateredText = userPlant.lastWateredDate?.let { timestamp ->
        val timeAgo = DateUtils.getRelativeTimeSpanString(
            timestamp,
            System.currentTimeMillis(),
            DateUtils.MINUTE_IN_MILLIS
        )
        "Watered $timeAgo"
    } ?: "Not watered yet"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onItemClick(userPlant.userPlantId) },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Plant image and info header
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Plant image
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    if (!userPlant.imageUri.isNullOrEmpty()) {
                        AsyncImage(
                            model = ImageRequest.Builder(context)
                                .data(userPlant.imageUri)
                                .crossfade(true)
                                .build(),
                            contentDescription = "User's plant image",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        // Placeholder if no image is available
                        Image(
                            painter = painterResource(id = R.drawable.ic_plant_placeholder),
                            contentDescription = "Plant placeholder",
                            modifier = Modifier.size(40.dp),
                            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSecondaryContainer)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Plant info
                Column(
                    modifier = Modifier
                        .weight(1f)
                ) {
                    // Plant name - use nickname if available, otherwise common name
                    val plantName = userPlant.nickname ?: plant?.commonName
                    Text(
                        text = plantName!!,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Planting date
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = "Planted date",
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.tertiary
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "$plantedAgo ",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

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

                Spacer(modifier = Modifier.width(8.dp))

                // Action buttons
                Column(
                    modifier = Modifier.padding(end = 16.dp),
                ) {
                    // Water button
                    IconButton(
                        onClick = { onWaterClick(userPlantWithDetails.userPlant.userPlantId) },
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer)
                    ) {
                        Icon(
                            imageVector = Icons.Default.WaterDrop,
                            contentDescription = "Water plant",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}
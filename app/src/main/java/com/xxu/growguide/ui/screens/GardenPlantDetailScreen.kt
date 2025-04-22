package com.xxu.growguide.ui.screens

import android.text.format.DateUtils
import android.util.Log
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.VerticalDivider
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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.xxu.growguide.R
import com.xxu.growguide.api.PlantsManager
import com.xxu.growguide.data.entity.PlantsEntity
import com.xxu.growguide.data.entity.UserPlantsEntity
import com.xxu.growguide.destinations.Destination
import com.xxu.growguide.ui.components.BackButton
import com.xxu.growguide.ui.viewmodels.GardenPlantDetailViewModel
import com.xxu.growguide.ui.viewmodels.GardenPlantDetailViewModelFactory
import com.xxu.growguide.ui.viewmodels.UserPlantWithDetails
import java.lang.Float.min
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

/**
 * Purpose: Displays detailed information about a specific user's garden plant.
 *
 * @param plantId The unique identifier of the plant to display
 * @param navController Navigation controller to handle screen navigation
 * @param innerPadding Padding values to apply to the screen content
 * @param viewModel ViewModel that provides the plant data and handles business logic
 */
@Composable
fun GardenPlantDetailScreen(
    userPlantId: Long,
    navController: NavHostController,
    innerPadding: PaddingValues,
    plantsManager: PlantsManager
) {
    val viewModel: GardenPlantDetailViewModel = viewModel(factory = GardenPlantDetailViewModelFactory(plantsManager))

    // Collect states from ViewModel
    val gardenPlantDetail by viewModel.gardenPlantDetail.collectAsState()
    val plantDetail by viewModel.plantDetail.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val wateringSuccess by viewModel.wateringSuccess.collectAsState()
    val deletingSuccess by viewModel.deletingSuccess.collectAsState()

    val plantId by remember { mutableStateOf(null) }

    // Confirmation dialog states
    var showDeleteDialog by remember { mutableStateOf(false) }
    var plantToDelete by remember { mutableStateOf<Long?>(null) }
    var showWaterDialog by remember { mutableStateOf(false) }
    var plantToWater by remember { mutableStateOf<Long?>(null) }

    // Load plant detail when screen is displayed
    LaunchedEffect(userPlantId) {
        viewModel.getGardenPlantWithDetail(userPlantId)
    }

    // Refresh current screen
    LaunchedEffect(wateringSuccess) {
        if(wateringSuccess) {
            viewModel.getGardenPlantWithDetail(userPlantId)
        }
    }

    // Navigate to upper level after removing a plant
    LaunchedEffect(deletingSuccess) {
        if(deletingSuccess) {
            navController.navigate(Destination.Garden.route)
        }
    }

    Column{
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
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
            // Content state
            else if (gardenPlantDetail != null) {
                val gardenPlant = gardenPlantDetail!!

                Log.i("GardenPlantDetailScreen", "gardenPlantDetail is not null")

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    // Plant image
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp)
                            .background(MaterialTheme.colorScheme.secondaryContainer),
                        contentAlignment = Alignment.Center
                    ) {

                        if (!gardenPlant.imageUri.isNullOrEmpty()) {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(gardenPlant.imageUri)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = "Plant photo",
                                contentScale = ContentScale.FillWidth,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            Image(
                                painter = painterResource(id = R.drawable.ic_plant_placeholder),
                                contentDescription = "Plant placeholder",
                                modifier = Modifier.size(80.dp),
                                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSecondaryContainer)
                            )
                        }
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        // Plant name
                        Text(
                            text = gardenPlant.nickname.toString(),
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        // Common name
                        Text(
                            text = plantDetail?.commonName.toString(),
                            style = MaterialTheme.typography.bodyLarge,
                            fontStyle = FontStyle.Italic,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Planting date
                        val dateFormatter = SimpleDateFormat("MMMM d, yyyy", Locale.getDefault())
                        val plantedDate = dateFormatter.format(Date(gardenPlant.plantingDate))

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = Color.Transparent
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CalendarMonth,
                                    contentDescription = "Planting date",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(24.dp)
                                )

                                Spacer(modifier = Modifier.width(16.dp))

                                Text(
                                    text = "Planted on $plantedDate.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }

                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 8.dp),
                            thickness = 1.dp,
                            color = MaterialTheme.colorScheme.outlineVariant
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // Action cards
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {
                            // Information Card
                            ActionCard(
                                icon = Icons.Default.Info,
                                title = "Information",
                                onClick = {
                                    val plantIdTemp = plantDetail?.id
                                    navController.navigate("plant_detail/$plantIdTemp")
                                },
                                modifier = Modifier.weight(1f)
                            )

                            // Guide Card
                            ActionCard(
                                icon = Icons.AutoMirrored.Filled.MenuBook,
                                title = "Guide",
                                onClick = { /* Toggle expanded state for guide */ },
                                modifier = Modifier.weight(1f)
                            )

                            // Delete Card
                            ActionCard(
                                icon = Icons.Default.Delete,
                                title = "Remove",
                                onClick = {
                                    plantToDelete = userPlantId
                                    showDeleteDialog = true
                                },
                                modifier = Modifier.weight(1f),
                                iconTint = MaterialTheme.colorScheme.error,
                            )
                        }
                        Spacer(modifier = Modifier.height(24.dp))

                        // Watering status
                        WateringStatus(
                            gardenPlant = gardenPlant,
                            plant = plantDetail,
                            onWaterClick = { userPlantId ->
                                plantToWater = userPlantId
                                showWaterDialog = true
                            },
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // Plant Notes
                        if (!gardenPlant.notes.isNullOrEmpty()) {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
                                ),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Column(
                                    modifier = Modifier
                                    .padding(12.dp)
                                ) {
                                    Text(
                                        text = "Notes",
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )

                                    HorizontalDivider(
                                        modifier = Modifier.padding(vertical = 8.dp),
                                        1.dp,
                                        MaterialTheme.colorScheme.surfaceContainerLow
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Text(
                                        text = gardenPlant.notes.toString(),
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(16.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(24.dp))
                        }

                        // Care instructions
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
                            ),
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                Text(
                                    text = "Care Instructions",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )

                                HorizontalDivider(
                                    modifier = Modifier.padding(vertical = 8.dp),
                                    1.dp,
                                    MaterialTheme.colorScheme.surfaceContainerLow
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Row() {
                                    // Watering instructions
                                    Row(
                                        modifier = Modifier.weight(1f),
                                        horizontalArrangement = Arrangement.Center,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.WaterDrop,
                                            contentDescription = "Watering",
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(24.dp)
                                        )

                                        Spacer(modifier = Modifier.width(16.dp))

                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = "Watering",
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )

                                            Text(
                                                text = plantDetail?.watering.toString(),
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }

                                    // Sunlight instructions
                                    Row(
                                        modifier = Modifier.weight(1f),
                                        horizontalArrangement = Arrangement.Center,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.WbSunny,
                                            contentDescription = "Sunlight",
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(24.dp)
                                        )

                                        Spacer(modifier = Modifier.width(16.dp))

                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = "Sunlight",
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )

                                            Text(
                                                text = plantDetail?.sunlight.toString(),
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // Bottom spacing
                        Spacer(modifier = Modifier.height(30.dp))
                    }
                }
            }

            // Floating back button - always visible at top left
            BackButton(navController)
        }
    }

    // Delete confirmation dialog
    if (showDeleteDialog && plantToDelete != null) {
        AlertDialog(
            onDismissRequest = {
                showDeleteDialog = false
                plantToDelete = null
            },
            title = { Text("Remove Plant") },
            text = { Text("Are you sure you want to remove this plant from your garden? This action cannot be undone.") },
            confirmButton = {
                Button(
                    onClick = {
                        plantToDelete?.let { userPlantId ->
                            viewModel.removeUserPlant(userPlantId)
                        }
                        navController.navigateUp()
                        showDeleteDialog = false
                        plantToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Remove")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Water confirmation dialog
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
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun ActionCard(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    iconTint: Color = MaterialTheme.colorScheme.primary,
    cardColor: Color = MaterialTheme.colorScheme.surfaceContainerLow
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier
                .size(60.dp)
                .clickable(onClick = onClick),
            colors = CardDefaults.cardColors(
                containerColor = cardColor
            ),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceContainer)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = iconTint,
                    modifier = Modifier.size(32.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
    }
}

/**
 * Purpose: Display watering status and information
 *
 * @param plant The plant details to display watering information for
 */
@Composable
fun WateringStatus(
    gardenPlant: UserPlantsEntity,
    plant: PlantsEntity?,
    onWaterClick: (Long) -> Unit
) {
    val context = LocalContext.current

    Log.i("GardenPlantDetailScreen", "WateringStatus - plant:${plant}")

    // Determine watering frequency based on plant requirements
    val (wateringDays, wateringText) = when {
        plant!!.watering.contains("frequent", ignoreCase = true) -> Pair(2, "Every 2-3 days")
        plant.watering.contains("average", ignoreCase = true) -> Pair(7, "Weekly")
        plant.watering.contains("minimum", ignoreCase = true) -> Pair(14, "Every 2-3 weeks")
        else -> Pair(7, "When soil is dry")
    }

    // Calculate watering status
    //val lastWatered = gardenPlant.lastWateredDate ?: gardenPlant.dateAdded
    //val daysSinceWatering = TimeUnit.MILLISECONDS.toDays(System.currentTimeMillis() - lastWatered)

    // TESTING ONLY - Remove or comment out before production
    //val testDaysOffset = wateringDays * 0.9 // Change this value to test different scenarios
    // 0 = just watered
    // 1 = 1 day since watering
    // 5 = 5 days since watering
    // testDaysOffset = 0 → Should show full progress bar (just watered)
    // testDaysOffset = wateringDays/2 → Should show around 50% progress
    // testDaysOffset = wateringDays * 0.7 → Should show "Will need water soon"
    // testDaysOffset = wateringDays * 0.9 → Should show "Time to water this plant"
    // testDaysOffset = wateringDays * 1.2 → Should show "This plant needs water urgently!"

//    val daysSinceWatering = if (testDaysOffset > 0) {
//        testDaysOffset.toLong()
//    } else {
//        TimeUnit.MILLISECONDS.toDays(System.currentTimeMillis() - lastWatered)
//    }

    // Reverse the progress calculation - starts at 1.0 (full) and decreases to 0.0 (empty)
    // Set watering progress specifically for never-watered plants
    val wateringProgress = if (gardenPlant.lastWateredDate == null) {
        // If never watered, show empty progress bar
        0f
    } else {
        // Calculate watering status for plants that have been watered before
        val lastWatered = gardenPlant.lastWateredDate
        val daysSinceWatering = TimeUnit.MILLISECONDS.toDays(System.currentTimeMillis() - lastWatered)

        // Reverse the progress calculation - starts at 1.0 (full) and decreases to 0.0 (empty)
        if (wateringDays > 0) {
            // Clamp between 0 and 1
            (1f - (daysSinceWatering.toFloat() / wateringDays.toFloat())).coerceIn(0f, 1f)
        } else 1f
    }

    // Format last watered date
    val lastWateredText = if (gardenPlant.lastWateredDate != null) {
        DateUtils.getRelativeTimeSpanString(
            gardenPlant.lastWateredDate,
            System.currentTimeMillis(),
            DateUtils.DAY_IN_MILLIS
        ).toString()
    } else {
        "Not watered yet"
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
        ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Watering information
                Text(
                    modifier = Modifier.weight(1f),
                    text = "Watering",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

//                IconButton(
//                    onClick = {  },
//                ) {
//                    Icon(
//                        imageVector = Icons.Default.MoreHoriz,
//                        contentDescription = "More",
//                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
//                        modifier = Modifier.size(24.dp)
//                    )
//                }
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 8.dp),
                1.dp,
                MaterialTheme.colorScheme.surfaceContainerLow
            )
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Last watered",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Text(
                    text = lastWateredText,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Watering status text
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Recommendation",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Text(
                    text = wateringText,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(30.dp))

            // Watering progress indicator with reversed colors
            LinearProgressIndicator(
                progress = { wateringProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(24.dp)
                    .clip(RoundedCornerShape(16.dp)),
                color = when {
                    wateringProgress < 0.3f -> MaterialTheme.colorScheme.error
                    wateringProgress < 0.5f -> MaterialTheme.colorScheme.tertiary
                    else -> MaterialTheme.colorScheme.secondaryContainer
                },
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
                strokeCap = StrokeCap.Square
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Watering status message with reversed logic
            Text(
                text = when {
                    wateringProgress < 0.3f -> "This plant needs water urgently!"
                    wateringProgress < 0.5f -> "Time to water this plant"
                    wateringProgress < 0.8f -> "Will need water soon"
                    else -> "Water level is good"
                },
                style = MaterialTheme.typography.bodyMedium,
                color = when {
                    wateringProgress < 0.3f -> MaterialTheme.colorScheme.error
                    wateringProgress < 0.5f -> MaterialTheme.colorScheme.tertiary
                    else -> MaterialTheme.colorScheme.primary
                },
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Water button
            IconButton(
                onClick = { onWaterClick(gardenPlant.userPlantId) },
                modifier = Modifier
                    .padding(horizontal = 30.dp)
                    .fillMaxWidth()
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
            ) {
                Row{
                    Icon(
                        imageVector = Icons.Default.WaterDrop,
                        contentDescription = "Water plant",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Record Watered Plant",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }
    }
}

package com.xxu.growguide.ui.screens

import android.text.format.DateUtils
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ChevronRight
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
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.graphics.ColorFilter
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

    val plantId by remember { mutableStateOf(null) }

    // Confirmation dialog states
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showWaterDialog by remember { mutableStateOf(false) }

    // Load plant detail when screen is displayed
    LaunchedEffect(userPlantId) {
        viewModel.getGardenPlantWithDetail(userPlantId)
    }

    Column(
        modifier = Modifier
            .padding(8.dp)
    ) {
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
                                contentScale = ContentScale.Crop,
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
                            .padding(16.dp)
                    ) {
                        // Plant name
                        Text(
                            text = gardenPlant.nickname ?: plantDetail?.commonName.toString(),
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        // Scientific name
                        Text(
                            text = plantDetail?.scientificName.toString(),
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
                                containerColor = MaterialTheme.colorScheme.surface
                            ),
                            border = CardDefaults.outlinedCardBorder(),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CalendarMonth,
                                    contentDescription = "Planting date",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(24.dp)
                                )

                                Spacer(modifier = Modifier.width(16.dp))

                                Column {
                                    Text(
                                        text = "Planted on",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )

                                    Text(
                                        text = plantedDate,
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )

                                    // Calculate age
                                    val daysSincePlanting = TimeUnit.MILLISECONDS.toDays(
                                        System.currentTimeMillis() - gardenPlant.plantingDate
                                    )

                                    Text(
                                        text = when {
                                            daysSincePlanting < 1 -> "Added today"
                                            daysSincePlanting == 1L -> "1 day old"
                                            daysSincePlanting < 30 -> "$daysSincePlanting days old"
                                            daysSincePlanting < 365 -> "${daysSincePlanting / 30} months old"
                                            else -> "${daysSincePlanting / 365} years old"
                                        },
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Watering information
                        Text(
                            text = "Watering",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        WateringStatus(gardenPlant, plantDetail)

                        Spacer(modifier = Modifier.height(24.dp))

                        // Plant Notes (if any)
                        if (gardenPlant.notes.isNullOrEmpty()) {
                            Text(
                                text = "Notes",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                                ),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = gardenPlant.notes.toString(),
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(16.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(24.dp))
                        }

                        // Care instructions
                        Text(
                            text = "Care Instructions",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            ),
                            border = CardDefaults.outlinedCardBorder(),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                // Watering instructions
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
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

                                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                                // Sunlight instructions
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
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

                        Spacer(modifier = Modifier.height(24.dp))

                        // Plant information button
                        TextButton(
                            onClick = {
                                val plantId = plantDetail?.id
                                navController.navigate("plant_detail/$plantId")
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("View full plant information")
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = "View details"
                            )
                        }

                        // Bottom spacing
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            }
        }
    }

    // Delete confirmation dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Remove Plant") },
            text = { Text("Are you sure you want to remove this plant from your garden? This action cannot be undone.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.removeUserPlant(gardenPlantDetail?.userPlantId?.toLong() ?: 0L)
                        navController.navigateUp()
                        showDeleteDialog = false
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
    if (showWaterDialog) {
        AlertDialog(
            onDismissRequest = { showWaterDialog = false },
            title = { Text("Water Plant") },
            text = { Text("Record that you've watered this plant today?") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.recordWatering(gardenPlantDetail?.userPlantId)
                        showWaterDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Yes, I watered it")
                }
            },
            dismissButton = {
                TextButton(onClick = { showWaterDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

/**
 * Purpose: Display watering status and information
 *
 * @param plant The plant details to display watering information for
 */
@Composable
fun WateringStatus(gardenPlant: UserPlantsEntity, plant: PlantsEntity?) {
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
    val lastWatered = gardenPlant.lastWateredDate ?: gardenPlant.dateAdded
    val daysSinceWatering = TimeUnit.MILLISECONDS.toDays(System.currentTimeMillis() - lastWatered)
    val wateringProgress = if (wateringDays > 0) {
        min(1f, daysSinceWatering.toFloat() / wateringDays.toFloat())
    } else 0f

    // Format last watered date
    val lastWateredText = if (gardenPlant.lastWateredDate != null) {
        DateUtils.getRelativeTimeSpanString(
            lastWatered,
            System.currentTimeMillis(),
            DateUtils.DAY_IN_MILLIS
        ).toString()
    } else {
        "Not watered yet"
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = CardDefaults.outlinedCardBorder(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
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

            // Watering progress indicator
            LinearProgressIndicator(
                progress = { wateringProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = when {
                    wateringProgress > 0.9f -> MaterialTheme.colorScheme.error
                    wateringProgress > 0.7f -> MaterialTheme.colorScheme.tertiary
                    else -> MaterialTheme.colorScheme.primary
                },
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Watering status text
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Watering recommendation",
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

            Spacer(modifier = Modifier.height(8.dp))

            // Watering status message
            Text(
                text = when {
                    daysSinceWatering >= wateringDays * 1.5 -> "This plant needs water urgently!"
                    daysSinceWatering >= wateringDays -> "Time to water this plant"
                    daysSinceWatering >= wateringDays * 0.7 -> "Will need water soon"
                    else -> "Water level is good"
                },
                style = MaterialTheme.typography.bodyMedium,
                color = when {
                    daysSinceWatering >= wateringDays * 1.2 -> MaterialTheme.colorScheme.error
                    daysSinceWatering >= wateringDays * 0.7 -> MaterialTheme.colorScheme.tertiary
                    else -> MaterialTheme.colorScheme.primary
                },
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Set reminder button (UI only, not functional here)
            TextButton(
                onClick = { /* Set reminder functionality would go here */ },
                modifier = Modifier.align(Alignment.End)
            ) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "Set reminder",
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Set watering reminder")
            }
        }
    }
}

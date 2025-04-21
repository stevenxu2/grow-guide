package com.xxu.growguide.ui.screens

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.xxu.growguide.R
import com.xxu.growguide.api.PlantsManager
import com.xxu.growguide.data.database.AppDatabase
import com.xxu.growguide.data.database.PlantsDao
import com.xxu.growguide.data.entity.PlantsEntity
import com.xxu.growguide.ui.viewmodels.PlantDetailViewModel

/**
 * Purpose: Displays detailed information about a specific plant.
 *
 * @param plantId The unique identifier of the plant to display
 * @param navController Navigation controller to handle screen navigation
 * @param innerPadding Padding values to apply to the screen content
 * @param viewModel ViewModel that provides the plant data and handles business logic
 */
@Composable
fun PlantDetailScreen(
    plantId: Int,
    navController: NavHostController,
    innerPadding: PaddingValues,
    viewModel: PlantDetailViewModel,
    plantsManager: PlantsManager,
    database: AppDatabase
) {
    // Collect states from ViewModel
    val plantDetail by viewModel.plantDetail.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    Log.i("PlantDetailScreen", "plantId: ${plantId}, plantDetail: ${plantDetail}, isLoading: ${isLoading}" )

    // Load plant detail when screen is displayed
    LaunchedEffect(plantId) {
        viewModel.loadPlantDetail(plantId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
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
        else if (plantDetail != null) {
            val plant = plantDetail!!

            Column(
                modifier = Modifier
                    .weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                        ) {
                            // Plant image
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(250.dp)
                            ) {
                                if (plant.imageUrl.isNotEmpty()) {
                                    AsyncImage(
                                        model = ImageRequest.Builder(LocalContext.current)
                                            .data(plant.imageUrl)
                                            .crossfade(true)
                                            .build(),
                                        contentDescription = plant.commonName,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                } else {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(MaterialTheme.colorScheme.tertiaryContainer),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Image(
                                            painter = painterResource(id = R.drawable.ic_plant_placeholder),
                                            contentDescription = "Plant Placeholder",
                                            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onTertiaryContainer),
                                            modifier = Modifier.size(80.dp)
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Plant information
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp)
                        ) {
                            // Plant name
                            Text(
                                text = plant.commonName,
                                style = MaterialTheme.typography.headlineLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )

                            // Scientific name
                            if (plant.scientificName.isNotEmpty()) {
                                Text(
                                    text = plant.scientificName,
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontStyle = FontStyle.Italic,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Basic info card
                            PlantBasicInfoCard(plant)

                            Spacer(modifier = Modifier.height(32.dp))

                            // Description
                            if (plant.description.isNotEmpty()) {
                                Text(
                                    text = "Description",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = plant.description,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )

                                Spacer(modifier = Modifier.height(32.dp))
                            }

                            // Care instructions
                            Text(
                                text = "Care Instructions",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // Watering
                            if (plant.watering.isNotEmpty()) {
                                CareItem(
                                    icon = Icons.Default.WaterDrop,
                                    title = "Watering",
                                    description = plant.watering
                                )
                            }

                            // Sunlight
                            if (plant.sunlight.isNotEmpty()) {
                                // Capitalizes the first letter
                                val formattedSunlight = plant.sunlight.split(", ").joinToString(", ") {
                                    it.trim().replaceFirstChar { char -> char.uppercase() }
                                }

                                CareItem(
                                    icon = Icons.Default.WbSunny,
                                    title = "Sunlight",
                                    description = formattedSunlight
                                )
                            }

                            // Maintenance
                            if (plant.maintenance.isNotEmpty() && plant.maintenance != "null") {
                                CareItem(
                                    icon = Icons.Default.Favorite,
                                    title = "Maintenance",
                                    description = plant.maintenance
                                )
                            }

                            // Growth rate
                            if (plant.growthRate.isNotEmpty()) {
                                CareItem(
                                    icon = Icons.Default.Timer,
                                    title = "Growth Rate",
                                    description = plant.growthRate
                                )
                            }

                            // Additional characteristics
                            Text(
                                text = "Characteristics",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                            )

                            // Characteristics grid
                            PlantCharacteristicsGrid(plant)

                            // Bottom spacing
                            Spacer(modifier = Modifier.height(24.dp))
                        }
                    }

                    // Floating back button - always visible at top left
                    Card(
                        modifier = Modifier
                            .padding(top = 12.dp, start = 12.dp)
                            .size(40.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f)
                        ),
                        shape = CircleShape,
                    ) {
                        IconButton(
                            onClick = { navController.navigateUp() },
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

            }

            Column(
                modifier = Modifier
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                // Proceed Plant button
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    onClick = {
                        // Save plant and navigate back
                        navController.navigate("add_plant/${plantId}")
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text(
                        text = "Proceed",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }
    }
}

/**
 * Purpose: Displays a card with basic information about a plant.
 *
 * @param plant The plant entity containing the information to display
 */
@Composable
fun PlantBasicInfoCard(plant: PlantsEntity) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.outline
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Family
            if (plant.family.isNotEmpty() && plant.family != "null") {
                InfoRow(title = "Family", value = plant.family)
            }

            // Genus
            if (plant.genus.isNotEmpty()) {
                InfoRow(title = "Genus", value = plant.genus)
            }

            // Type
            if (plant.type.isNotEmpty()) {
                InfoRow(title = "Type", value = plant.type)
            }

            // Cycle
            if (plant.cycle.isNotEmpty()) {
                InfoRow(title = "Life Cycle", value = plant.cycle, isLast = true)
            }
        }
    }
}

/**
 * Purpose: Creates a row with a label and value for displaying plant information.
 *
 * @param title The label or category of information
 * @param value The specific value to display
 * @param isLast Boolean flag indicating if this is the last row (to hide divider)
 */
@Composable
fun InfoRow(title: String, value: String, isLast: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.background
        )

        Text(
            text = value.replaceFirstChar { char -> char.uppercase() },
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.background
        )
    }

    if (!isLast) {
        HorizontalDivider(
            modifier = Modifier.padding(vertical = 4.dp),
            color = MaterialTheme.colorScheme.background.copy(alpha = 0.2f)
        )
    }
}

/**
 * Purpose: Displays care instruction information with an icon, title, and description.
 *
 * @param icon The vector image to display beside the care instruction
 * @param title The title of the care instruction
 * @param description The detailed instruction text
 */
@Composable
fun CareItem(icon: ImageVector, title: String, description: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .size(24.dp)
                .padding(top = 2.dp)
        )

        Spacer(modifier = Modifier.size(12.dp))

        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * Purpose: Displays a grid of plant characteristics.
 *
 * @param plant The plant entity containing the characteristics data
 */
@Composable
fun PlantCharacteristicsGrid(plant: PlantsEntity) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        // Row 1
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            CharacteristicItem(
                title = "Indoor",
                value = if (plant.indoor) "Yes" else "No",
                modifier = Modifier.weight(1f)
            )

            CharacteristicItem(
                title = "Flowers",
                value = if (plant.flowers) "Yes" else "No",
                modifier = Modifier.weight(1f)
            )

            CharacteristicItem(
                title = "Fruits",
                value = if (plant.fruits) "Yes" else "No",
                modifier = Modifier.weight(1f)
            )
        }

        // Row 2
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        ) {
            CharacteristicItem(
                title = "Drought Tolerant",
                value = if (plant.droughtTolerant) "Yes" else "No",
                modifier = Modifier.weight(1f)
            )

            CharacteristicItem(
                title = "Salt Tolerant",
                value = if (plant.saltTolerant) "Yes" else "No",
                modifier = Modifier.weight(1f)
            )

            CharacteristicItem(
                title = "Care Level",
                value = plant.careLevel.ifEmpty { "Not specified" },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

/**
 * Purpose: Displays a single characteristic item with a title and value.
 *
 * @param title The name of the characteristic
 * @param value The value of the characteristic
 * @param modifier Optional Modifier for customizing the layout
 */
@Composable
fun CharacteristicItem(title: String, value: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
    }
}
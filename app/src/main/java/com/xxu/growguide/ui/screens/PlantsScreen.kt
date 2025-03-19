package com.xxu.growguide.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.xxu.growguide.R

@Composable
fun PlantsScreen(navController: NavHostController, innerPadding: PaddingValues, scrollState: ScrollState) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(innerPadding)
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        PlantsHeader()
        SearchPlant()
        PlantList()
    }
}

/**
 * Display the header of the screen
 */
@Composable
fun PlantsHeader(){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Header
        Text(
            text = "My Plants",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            fontSize = 32.sp,
            color = MaterialTheme.colorScheme.onSurface
        )

        // Add icon
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer)
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add",
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.size(24.dp)
            )
        }
    }
    Spacer(modifier = Modifier.height(16.dp))
}


/**
 * Search bar
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchPlant(){
    Row(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        SearchBar(
            query = "",
            onQueryChange = { },
            onSearch = { },
            active = false,
            onActiveChange = { },
            placeholder = {
                Text(
                    "Search plants...",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            colors = SearchBarDefaults.colors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                dividerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                inputFieldColors = SearchBarDefaults.inputFieldColors(
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            ),
            modifier = Modifier
                .fillMaxWidth()
        ) {
            // Search
        }
    }
    Spacer(modifier = Modifier.height(24.dp))
}

/**
 * Define a plant data for testing
 */
data class PlantData(
    val name: String,
    val plantedDate: String,
    val status: String
)

/**
 * List of my plants
 */
@Composable
fun PlantList() {
    // Mock data for testing
    val testPlants = listOf(
        PlantData("Tomato", "Planted: 21 days ago", "Growing"),
        PlantData("Basil", "Planted: 5 days ago", "Healthy"),
        PlantData("Lavender", "Planted: 10 day sago", "Needs water"),
        PlantData("Mint", "Planted: 7 days ago", "Healthy"),
        PlantData("Rosemary", "Planted: 2 days ago", "Growing"),
        PlantData("Sunflower", "Planted: yesterday", "Seedling"),
        PlantData("Cucumber", "Planted: 30 days ago", "Needs attention")
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
    ) {
        items(testPlants) { plant ->
            PlantCard(plant)
        }
    }
}

/**
 * A single plant card
 */
@Composable
fun PlantCard(plant: PlantData){
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceContainerLowest)
                .border(1.dp, MaterialTheme.colorScheme.surfaceDim, shape = RoundedCornerShape(12.dp))
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.tertiaryContainer)
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        modifier = Modifier.size(80.dp),
                        painter = painterResource(id = R.drawable.ic_plant_placeholder),
                        contentDescription = "Plant Placeholder",
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onTertiaryContainer)
                    )
                }
            }
            Column(
                modifier = Modifier.weight(2f)
            ) {
                Column {
                    Text(
                        text = plant.name,
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = plant.plantedDate,
                        style = MaterialTheme.typography.bodyMedium,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = plant.status,
                        style = MaterialTheme.typography.bodyMedium,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

        }

    }
    Spacer(modifier = Modifier.height(16.dp))
}
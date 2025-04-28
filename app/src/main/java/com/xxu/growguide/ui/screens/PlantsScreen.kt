package com.xxu.growguide.ui.screens

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.xxu.growguide.R
import com.xxu.growguide.api.PlantsManager
import com.xxu.growguide.data.database.AppDatabase
import com.xxu.growguide.data.database.PlantsDao
import com.xxu.growguide.data.models.plants.list.Data
import com.xxu.growguide.ui.viewmodels.PlantsViewModel
import com.xxu.growguide.ui.viewmodels.PlantsViewModelFactory

/**
 * Purpose: Displays a screen showing a list of plants with search functionality
 *
 * @param navController Navigation controller for screen navigation
 * @param innerPadding Padding values from the parent layout
 * @param scrollState State object for handling scrolling behavior
 * @param plantsViewModel ViewModel that provides plant data and search functionality
 */
@Composable
fun PlantsScreen(
    navController: NavHostController,
    innerPadding: PaddingValues,
    scrollState: ScrollState,
    plantsManager: PlantsManager,
    database: AppDatabase
) {
    val factory = remember {
        PlantsViewModelFactory(plantsManager, database.plantsDao())
    }

    val viewModel: PlantsViewModel = viewModel(factory = factory)

    // Collect states from ViewModel
    val plants by viewModel.plants.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val resetScroll by viewModel.resetScroll.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        // Main content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
        ) {
            PlantsHeader()

            SearchPlant(viewModel = viewModel)

            if (isLoading && plants.isEmpty()) {
                // Show loading indicator when initially loading
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            } else if (error != null && plants.isEmpty()) {
                // Show error message if there's an error and no data
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Error: $error",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            } else {
                // Show plant list
                PlantList(
                    plants = plants,
                    isLoading = isLoading,
                    resetScroll = resetScroll,
                    onSearch = { viewModel.clearScrollReset() },
                    onLoadMore = { viewModel.loadMorePlants() },
                    onPlantClick = { plantId ->
                        navController.navigate("plant_detail/$plantId")
                    },
                    navController = navController
                )
            }
        }
    }
}

/**
 * Purpose: Displays the header section of the plants screen with title and add button
 */
@Composable
fun PlantsHeader(){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Header
        Text(
            text = "Plants",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
    Spacer(modifier = Modifier.height(16.dp))
}


/**
 * Purpose: Provides a search bar for filtering plants by name and scientific name
 *
 * @param plantsViewModel ViewModel that handles the search functionality
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchPlant(
    viewModel: PlantsViewModel
){
    val searchQuery by viewModel.searchQuery.collectAsState()

    Row(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        SearchBar(
            query = searchQuery,
            onQueryChange = { viewModel.updateSearchQuery(it) },
            onSearch = { viewModel.searchPlants(searchQuery) },
            active = false,
            onActiveChange = { },
            placeholder = {
                Text(
                    "Search for plants",
                    style = MaterialTheme.typography.bodyLarge
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { viewModel.updateSearchQuery("") }) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Clear",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            },
            colors = SearchBarDefaults.colors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                dividerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
                inputFieldColors = SearchBarDefaults.inputFieldColors(
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            ),
            modifier = Modifier
                .fillMaxWidth()
        ) {
            // Search body
        }
    }
    Spacer(modifier = Modifier.height(24.dp))
}

/**
 * Purpose: Displays a scrollable list of plants with lazy loading
 *
 * @param plants List of plant data to display
 * @param isLoading Boolean indicating if more plants are being loaded
 * @param resetScroll Boolean flag to reset the scroll position
 * @param onSearch Callback function when search is performed
 * @param onLoadMore Callback function to load more plants when scrolling near the end
 * @param onPlantClick Callback function when a plant is clicked, with plant ID parameter
 */
@Composable
fun PlantList(
    plants: List<Data?>,
    isLoading: Boolean,
    resetScroll: Boolean = false,
    onSearch: () -> Unit,
    onLoadMore: () -> Unit,
    onPlantClick: (Int) -> Unit,
    navController: NavHostController
) {
    val listState = rememberLazyListState()

    LaunchedEffect(resetScroll) {
        listState.animateScrollToItem(0)
        onSearch()
    }

    // This LaunchedEffect monitors for scrolling close to the end of the list
    LaunchedEffect(listState, plants.size) {
        snapshotFlow {
            val layoutInfo = listState.layoutInfo
            val totalItems = layoutInfo.totalItemsCount
            val lastVisibleItem = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0

            // Check if we're near the end of the list
            (lastVisibleItem >= totalItems - 3) && totalItems > 0
        }.collect { isAtEnd ->
            //Log.i("PlantsScreen", "At the end is $isAtEnd, isLoading: $isLoading, plantsIsNotEmpty: ${plants.isNotEmpty()}")
            if (isAtEnd && !isLoading && plants.isNotEmpty()) {
                onLoadMore()
            }
        }
    }

    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(plants) { plant ->
            plant?.let {
                PlantCard(
                    plant = it,
                    navController = navController,
                    onClick = { it.id?.let { id -> onPlantClick(id) } }
                )
            }
        }

        // Show loading indicator at the end when loading more
        if (isLoading && plants.isNotEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(40.dp),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

/**
 * Purpose: Displays a single plant card with image and basic information
 *
 * @param plant The plant data to display
 * @param onClick Callback function when the card is clicked
 */
@Composable
private fun PlantCard(
    plant: Data,
    navController: NavHostController,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
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
                if (plant.defaultImage?.smallUrl?.isNotEmpty() == true) {
                    // Load image from URL
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(plant.defaultImage?.smallUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = plant.commonName,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(120.dp)
                    )
                } else {
                    // Show placeholder
                    Image(
                        modifier = Modifier.size(80.dp),
                        painter = painterResource(id = R.drawable.ic_plant_placeholder),
                        contentDescription = "Plant Placeholder",
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimaryContainer)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Plant details
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = plant.commonName.toString(),
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = plant.scientificName?.firstOrNull().toString(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 12.sp,
                )
            }

            // Add icon with shadow
            Card(
                modifier = Modifier
                    .padding(end = 8.dp)
                    .size(40.dp),
                shape = CircleShape,
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 4.dp
                )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add",
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .clickable {
                                plant.id?.let { plantId ->
                                    navController.navigate("add_plant/$plantId")
                                }
                            }
                    )
                }
            }
        }
    }

    Spacer(modifier = Modifier.height(8.dp))
}
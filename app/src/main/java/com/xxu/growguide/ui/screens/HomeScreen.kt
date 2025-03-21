package com.xxu.growguide.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import com.xxu.growguide.ui.theme.Sunny

@Composable
fun HomeScreen(navController: NavHostController, innerPadding: PaddingValues, scrollState: ScrollState) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(innerPadding)
            .padding(horizontal = 20.dp, vertical = 16.dp)
            .verticalScroll(scrollState)
    ) {
        HomeHeader()
        WeatherCard()
        TodayTasks()
        MyPlants()
        CommunityUpdates()
    }
}

/**
 * Display the header of the screen
 */
@Composable
fun HomeHeader(){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Header
        Text(
            text = "My Garden",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            fontSize = 32.sp,
            color = MaterialTheme.colorScheme.onSurface
        )

        // Profile icon
//        Box(
//            modifier = Modifier
//                .size(48.dp)
//                .clip(CircleShape)
//                .background(MaterialTheme.colorScheme.primaryContainer)
//                .padding(8.dp),
//            contentAlignment = Alignment.Center
//        ) {
//            Icon(
//                imageVector = Icons.Default.Person,
//                contentDescription = "Profile",
//                tint = MaterialTheme.colorScheme.onPrimaryContainer,
//                modifier = Modifier.size(24.dp)
//            )
//        }
    }

    Spacer(modifier = Modifier.height(16.dp))
}

/**
 * Weather information
 */
@Composable
fun WeatherCard(){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.tertiaryContainer)
            .padding(16.dp),
    ) {
        Column(
            modifier = Modifier.weight(3f)
        ) {
            Text(
                text = "Today's weather",
                style = MaterialTheme.typography.bodySmall,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "18°C",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                fontSize = 32.sp,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Sunny, perfect for watering",
                style = MaterialTheme.typography.bodyMedium,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.primary
            )
        }
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                modifier = Modifier.size(80.dp),
                painter = painterResource(id = R.drawable.ic_sun),
                contentDescription = "Sun",
                colorFilter = ColorFilter.tint(Sunny)
            )
        }
    }

    Spacer(modifier = Modifier.height(24.dp))
}

/**
 * Display user's tasks
 */
@Composable
fun TodayTasks(){
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
        ) {
            Text(
                text = "Today's Tasks",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        TaskCard("Water Tomatoes", "Due today")
        TaskCard("Water Strawberries", "Due tomorrow")

    }
    Spacer(modifier = Modifier.height(24.dp))
}

/**
 * Task card to display a task
 */
@Composable
fun TaskCard(title: String, dueText: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerLow)
            .padding(16.dp),

        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Checkbox(
                checked = true,
                onCheckedChange = null,
                colors = CheckboxDefaults.colors(
                    checkmarkColor = MaterialTheme.colorScheme.onPrimary,
                    uncheckedColor = MaterialTheme.colorScheme.primary,
                    checkedColor = MaterialTheme.colorScheme.primary
                )
            )
        }
        Column(
            modifier = Modifier.weight(6f)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
            )
            Spacer(modifier = Modifier.size(4.dp))
            Text(
                text = dueText,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 16.sp,
            )
        }
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Default.KeyboardArrowRight,
                contentDescription = "arrow",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(24.dp)
            )
        }
    }
    Spacer(modifier = Modifier.height(8.dp))
}

/**
 * Display user's plants
 */
@Composable
fun MyPlants(){
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        // header
        Row(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(
                text = "My Plants",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        Spacer(modifier = Modifier.height(8.dp))

        // card
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceContainerLowest)
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
                    Icon(
                        painter = painterResource(id = R.drawable.ic_plant_placeholder),
                        contentDescription = "Profile",
                        tint = MaterialTheme.colorScheme.onTertiaryContainer,
                        modifier = Modifier.size(60.dp)
                    )
                }
            }
            Column(
                modifier = Modifier.weight(2f)
            ) {
                Column {
                    Text(
                        text = "Cherry Tomatoes",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Planted: 21 days ago",
                        style = MaterialTheme.typography.bodyMedium,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Growing well",
                        style = MaterialTheme.typography.bodyMedium,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

        }

    }
    Spacer(modifier = Modifier.height(24.dp))
}

/**
 * Display the newest community posts
 */
@Composable
fun CommunityUpdates(){
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        // header
        Row(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(
                text = "Community Updates",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        Spacer(modifier = Modifier.height(8.dp))

        // card
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceContainerLowest)
                .padding(16.dp),
        ) {
            Post("Get started with house plants")
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 12.dp),
                color = MaterialTheme.colorScheme.surfaceContainerLow
            )
            Post("Houseplant Care Tips")
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 12.dp),
                color = MaterialTheme.colorScheme.surfaceContainerLow
            )
            Post("15 Brilliant Plant Care Tips")
        }
    }
    Spacer(modifier = Modifier.height(24.dp))
}

@Composable
fun Post(title: String) {
    Row {
        Column(
            modifier = Modifier.width(36.dp)
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
                    style = MaterialTheme.typography.bodyMedium,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "View 3 comments",
                    style = MaterialTheme.typography.bodyMedium,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
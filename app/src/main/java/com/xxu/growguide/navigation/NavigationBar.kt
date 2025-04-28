package com.xxu.growguide.navigation
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.xxu.growguide.R
import com.xxu.growguide.destinations.Destination

/**
 * Purpose: Creates a bottom navigation bar with tabs for Home, Plants, Community, and Profile
 *
 * @param navController The NavController used to handle navigation between destinations
 */
@Composable
fun BottomNav(navController: NavController) {
    // interactive colors
    val itemColors = NavigationBarItemDefaults.colors(
        selectedIconColor = MaterialTheme.colorScheme.primary,
        selectedTextColor = MaterialTheme.colorScheme.primary,
        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
        indicatorColor = MaterialTheme.colorScheme.surfaceContainerLow
    )

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
    ) {
        val navBackStackEntry = navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry.value?.destination

        val icHome = painterResource(id = R.drawable.ic_home)
        val icPlants = painterResource(id = R.drawable.ic_plants)
        val icGarden = painterResource(id = R.drawable.ic_garden)
        val icCommunity = painterResource(id = R.drawable.ic_community)
        val icProfile = painterResource(id = R.drawable.ic_profile)

        NavigationBarItem(
            selected = currentDestination?.route == Destination.Home.route,
            onClick = { navController.navigate(Destination.Home.route) {
                popUpTo(Destination.Home.route)
                launchSingleTop = true
            }},
            icon = { Icon(painter = icHome, contentDescription = null)},
            label = { Text(text = Destination.Home.route) },
            colors = itemColors
        )
        NavigationBarItem(
            selected = currentDestination?.route == Destination.Plants.route,
            onClick = { navController.navigate(Destination.Plants.route) {
                popUpTo(Destination.Plants.route)
                launchSingleTop = true
            }},
            icon = { Icon(painter = icPlants, contentDescription = null)},
            label = { Text(text = Destination.Plants.route) },
            colors = itemColors
        )
        NavigationBarItem(
            selected = currentDestination?.route == Destination.Garden.route,
            onClick = { navController.navigate(Destination.Garden.route) {
                popUpTo(Destination.Garden.route)
                launchSingleTop = true
            }},
            icon = { Icon(painter = icGarden, contentDescription = null)},
            label = { Text(text = Destination.Garden.route) },
            colors = itemColors
        )
//        NavigationBarItem(
//            selected = currentDestination?.route == Destination.Community.route,
//            onClick = { navController.navigate(Destination.Community.route) {
//                popUpTo(Destination.Community.route)
//                launchSingleTop = true
//            }},
//            icon = { Icon(painter = icCommunity, contentDescription = null)},
//            label = { Text(text = Destination.Community.route) },
//            colors = itemColors
//        )
        NavigationBarItem(
            selected = currentDestination?.route == Destination.Profile.route,
            onClick = { navController.navigate(Destination.Profile.route) {
                popUpTo(Destination.Profile.route)
                launchSingleTop = true
            }},
            icon = { Icon(painter = icProfile, contentDescription = null)},
            label = { Text(text = Destination.Profile.route) },
            colors = itemColors
        )
    }
}
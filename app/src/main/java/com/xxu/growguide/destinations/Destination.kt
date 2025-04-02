package com.xxu.growguide.destinations

/**
 * Purpose: Defines navigation destinations throughout the app
 *
 * @property route The unique route string for this destination
 */
sealed class Destination(val route: String) {
    object Home : Destination("Home")
    object Plants :  Destination("Plants")
    object Community : Destination("Community")
    object Profile : Destination("Profile")
    object Login : Destination("login")
    object AddPlant : Destination("add_plant")

    object PlantDetail : Destination("plant_detail") {
        const val plantIdArg = "plantId"
        val routeWithArgs = "$route/{$plantIdArg}"
        fun createRoute(plantId: Int) = "$route/$plantId"
    }

    object PlantCare : Destination("plant_care") {
        const val plantIdArg = "plantId"
        val routeWithArgs = "$route/{$plantIdArg}"
        fun createRoute(plantId: Int) = "$route/$plantId"
    }
}
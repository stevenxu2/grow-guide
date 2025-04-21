package com.xxu.growguide.destinations

/**
 * Purpose: Defines navigation destinations throughout the app
 *
 * @property route The unique route string for this destination
 */
sealed class Destination(val route: String) {
    object Home : Destination("Home")
    object Plants :  Destination("Plants")
    object Community : Destination("Shared")
    object Profile : Destination("Profile")
    object Garden : Destination("Garden")
    object Login : Destination("login")

    object AddPlant : Destination("add_plant") {
        const val plantIdArg = "plantId"
        val routeWithArgs = "$route/{$plantIdArg}"
        fun createRoute(plantId: Int) = "$route/$plantId"
    }

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

    object GardenPlantDetail : Destination("garden_plant_detail") {
        const val userPlantIdArg = "userPlantId"
        val routeWithArgs = "$route/{$userPlantIdArg}"
        fun createRoute(userPlantId: Long) = "$route/$userPlantId"
    }
}
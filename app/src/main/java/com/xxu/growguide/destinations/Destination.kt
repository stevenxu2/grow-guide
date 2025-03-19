package com.xxu.growguide.destinations

sealed class Destination(val route: String) {
    object Home : Destination("Home")
    object Plants :  Destination("Plants")
    object Community : Destination("Community")
    object Profile : Destination("Profile")
}
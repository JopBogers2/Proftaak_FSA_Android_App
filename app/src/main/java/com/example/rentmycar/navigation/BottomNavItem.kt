package com.example.rentmycar.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(val route: String, val icon: ImageVector, val title: String) {
    data object Home : BottomNavItem("home", Icons.Default.Home, "Home")
    data object MyCars : BottomNavItem("myCars", Icons.Default.Star, "My Cars")
    data object Reservations :
        BottomNavItem("reservations", Icons.Default.DateRange, "Reservations")

    data object Profile : BottomNavItem("profile", Icons.Default.Person, "Profile")
}

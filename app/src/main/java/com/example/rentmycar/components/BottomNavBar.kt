package com.example.rentmycar.components

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.rentmycar.navigation.BottomNavItem

@Composable
fun BottomNavBar(navController: NavController) {
    val items = listOf(BottomNavItem.Home, BottomNavItem.Profile, BottomNavItem.Settings)
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
    if (items.any { it.route == currentRoute })
    NavigationBar {
        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.title) },
                label = { Text(item.title) },
                selected = currentRoute == item.route,
                onClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            launchSingleTop = true
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            restoreState = true
                        }
                    }
                }
            )
        }
    }
}

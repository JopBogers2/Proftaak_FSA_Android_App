package com.example.rentmycar.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.rentmycar.R
import com.example.rentmycar.navigation.BottomNavItem

@Composable
fun BottomNavBar(navController: NavController) {
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.MyCars,
        BottomNavItem.Reservations,
        BottomNavItem.Profile
    )
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route

    // Only show the navigation bar if the current route matches a bottom nav route
    if (items.any { it.route == currentRoute }) {
        NavigationBar {
            items.forEach { item ->
                val isSelected = currentRoute == item.route
                NavigationBarItem(
                    icon = {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.title
                        )
                    },
                    label = {
                        when (item) {
                            BottomNavItem.Home -> Text(text = stringResource(R.string.home))
                            BottomNavItem.MyCars -> Text(text = stringResource(R.string.my_cars))
                            BottomNavItem.Profile -> Text(text = stringResource(R.string.profile))
                            BottomNavItem.Reservations -> Text(text = stringResource(R.string.reservations))
                        }
                    },
                    selected = isSelected,
                    onClick = {
                        if (!isSelected) {
                            navController.navigate(item.route) {
                                launchSingleTop = true
                                popUpTo(navController.graph.startDestinationId) { saveState = true }
                                restoreState = true
                            }
                        }
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    modifier = Modifier.padding(horizontal = 4.dp) // Slight padding for spacing
                )
            }
        }
    }
}

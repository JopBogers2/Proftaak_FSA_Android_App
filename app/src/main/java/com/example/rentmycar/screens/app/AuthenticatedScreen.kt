package com.example.rentmycar.screens.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavHostController
import com.example.rentmycar.navigation.AppNavItem
import kotlinx.coroutines.flow.SharedFlow

@Composable
fun AuthenticatedScreen(
    navController: NavHostController,
    logoutEvent: SharedFlow<Unit>,
    content: @Composable () -> Unit
) {
    // Observe logout events and navigate to login
    LaunchedEffect(logoutEvent) {
        logoutEvent.collect {
            navController.navigate(AppNavItem.Login.route) {
                popUpTo(0) { inclusive = true } // Clear entire back stack
            }
        }
    }

    content()
}
package com.example.rentmycar.screens.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavHostController
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
            navController.navigate("login") {
                popUpTo(0) { inclusive = true } // Clear entire back stack
            }
        }
    }

    content()
}
package com.example.rentmycar.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.rentmycar.screens.auth.LoginScreen
import com.example.rentmycar.screens.auth.RegisterScreen
import com.example.rentmycar.screens.home.HomeScreen
import com.example.rentmycar.screens.home.ProfileScreen
import com.example.rentmycar.screens.home.SettingsScreen

@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(navController = navController, startDestination = getStartDestination()) {
        composable(BottomNavItem.Home.route) { HomeScreen(navController) }
        composable(BottomNavItem.Profile.route) { ProfileScreen(navController) }
        composable(BottomNavItem.Settings.route) { SettingsScreen(navController) }
        composable("login") { LoginScreen(navController) }
        composable("register") { RegisterScreen(navController) }
    }
}

fun getStartDestination(): String {
    return "login"
}

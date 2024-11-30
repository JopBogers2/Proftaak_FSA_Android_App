package com.example.rentmycar.navigation

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.rentmycar.screens.auth.LoginScreen
import com.example.rentmycar.screens.auth.RegisterScreen
import com.example.rentmycar.screens.home.HomeScreen
import com.example.rentmycar.screens.home.ProfileScreen
import com.example.rentmycar.screens.home.SettingsScreen
import com.example.rentmycar.datastore.JwtDataStore

@Composable
fun AppNavigation(navController: NavHostController, context: Context) {
    val startDestination = getStartDestination(context)
    NavHost(navController = navController, startDestination = startDestination) {
        composable(BottomNavItem.Home.route) { HomeScreen(navController) }
        composable(BottomNavItem.Profile.route) { ProfileScreen(navController) }
        composable(BottomNavItem.Settings.route) { SettingsScreen(navController) }
        composable("login") { LoginScreen(navController) }
        composable("register") { RegisterScreen(navController) }
    }
}

fun getStartDestination(context: Context): String {
    return if (JwtDataStore.hasToken(context)) {
        BottomNavItem.Home.route // If token exists, start at Home
    } else {
        "login" // Otherwise, start at Login
    }
}

package com.example.rentmycar.navigation

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.rentmycar.screens.app.HomeScreen
import com.example.rentmycar.screens.app.ProfileScreen
import com.example.rentmycar.screens.app.SettingsScreen
import com.example.rentmycar.PreferencesManager
import com.example.rentmycar.screens.app.EditProfileScreen
import com.example.rentmycar.screens.auth.LoginScreen
import com.example.rentmycar.screens.auth.RegisterScreen


@Composable
fun AppNavigation(navController: NavHostController, context: Context) {
    val startDestination = getStartDestination(context)
    NavHost(navController = navController, startDestination = startDestination) {
        composable(AppNavItem.Login.route) { LoginScreen(navController) }
        composable(AppNavItem.Register.route) { RegisterScreen(navController) }
        composable(BottomNavItem.Home.route) { HomeScreen(navController) }
        composable(BottomNavItem.Profile.route) { ProfileScreen(navController) }
        composable(AppNavItem.EditProfile.route) { EditProfileScreen(navController) }
        composable(BottomNavItem.Settings.route) { SettingsScreen(navController) }
    }
}

fun getStartDestination(context: Context): String {
    val token = PreferencesManager(context).jwtToken
    if (token != null) {
        return BottomNavItem.Home.route
    }
    return AppNavItem.Login.route
}

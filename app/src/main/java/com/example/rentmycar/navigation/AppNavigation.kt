package com.example.rentmycar.navigation

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.rentmycar.screens.app.HomeScreen
import com.example.rentmycar.screens.app.ProfileScreen
import com.example.rentmycar.PreferencesManager
import com.example.rentmycar.screens.app.CarItemScreen
import com.example.rentmycar.screens.app.DirectionScreen
import com.example.rentmycar.screens.app.EditProfileScreen
import com.example.rentmycar.screens.app.ReservationsScreen
import com.example.rentmycar.screens.app.AvailableTimeslotsScreen
import com.example.rentmycar.screens.auth.LoginScreen
import com.example.rentmycar.screens.auth.RegisterScreen


@Composable
fun AppNavigation(navController: NavHostController, context: Context) {
    val startDestination = getStartDestination(context)
    NavHost(navController = navController, startDestination = startDestination) {
        composable(AppNavItem.Login.route) { LoginScreen(navController) }
        composable(AppNavItem.Register.route) { RegisterScreen(navController) }
        composable(BottomNavItem.Home.route) { HomeScreen(navController, context) }
        composable(BottomNavItem.Profile.route) { ProfileScreen(navController) }
        composable(BottomNavItem.Reservations.route) { ReservationsScreen(navController) }
        composable(AppNavItem.EditProfile.route) { EditProfileScreen(navController) }
        composable("carItem/{carId}", arguments = listOf(
            navArgument("carId") {
                type = NavType.IntType
                defaultValue = -1
            }
        )
        ) { backStackEntry ->
            val carId = backStackEntry.arguments?.getInt("carId") ?: throw Error()
            CarItemScreen(navController, carId)
        }
        composable("directions/{carId}", arguments = listOf(
            navArgument("carId") {
                type = NavType.IntType
                defaultValue = -1
            }
        )
        ) { backStackEntry ->
            val carId = backStackEntry.arguments?.getInt("carId") ?: throw Error()
            DirectionScreen(navController, carId)
        }
        composable(
            "timeslots/{carId}",
            arguments = listOf(
                navArgument("carId") {
                    type = NavType.IntType
                    defaultValue = -1
                }
            )

        ) { backStackEntry ->
            val carId = backStackEntry.arguments?.getInt("carId") ?: throw Error()
            AvailableTimeslotsScreen(navController, carId)
        }
    }
}

fun getStartDestination(context: Context): String {
    val token = PreferencesManager(context).jwtToken
    if (token != null) {
        return BottomNavItem.Home.route
    }
    return AppNavItem.Login.route
}

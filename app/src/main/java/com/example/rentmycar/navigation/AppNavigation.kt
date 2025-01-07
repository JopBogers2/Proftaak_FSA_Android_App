package com.example.rentmycar.navigation

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.rentmycar.screens.app.HomeScreen
import com.example.rentmycar.screens.app.ProfileScreen
import com.example.rentmycar.screens.app.SettingsScreen
import com.example.rentmycar.PreferencesManager
import com.example.rentmycar.screens.app.AddCarScreen
import com.example.rentmycar.screens.app.EditProfileScreen
import com.example.rentmycar.screens.app.MyCarsScreen
import com.example.rentmycar.screens.auth.LoginScreen
import com.example.rentmycar.screens.auth.RegisterScreen
import com.example.rentmycar.viewmodel.MyCarViewModel


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
    composable("my_cars") {
            val viewModel: MyCarViewModel = hiltViewModel()
            MyCarsScreen(
                onNavigateToAddCar = { navController.navigate("add_car") },
                onNavigateToCarDetail = { carId -> navController.navigate("car_detail/$carId") },
                viewModel = viewModel
            )
        }
        composable("add_car") {
            val myCarViewModel: MyCarViewModel = hiltViewModel()
            AddCarScreen(navController, myCarViewModel)
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

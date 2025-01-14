package com.example.rentmycar.navigation

sealed class AppNavItem(val route: String) {
    data object Login : AppNavItem("login")
    data object Register : AppNavItem("register")
    data object EditProfile : AppNavItem("edit-profile")
    data object AddCar : AppNavItem("addCar")
    data object UserCars : AppNavItem("userCars")
}

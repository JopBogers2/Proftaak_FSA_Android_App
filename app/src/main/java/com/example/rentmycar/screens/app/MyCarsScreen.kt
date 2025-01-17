package com.example.rentmycar.screens.app

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.rentmycar.navigation.AppNavItem

@Composable
fun MyCarsScreen(navController: NavController) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("My Cars", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                try {
                    navController.navigate(AppNavItem.AddCar.route)
                } catch (e: Exception) {
                    Log.e("Navigation", "Error navigating to AddCar: ${e.message}", e)
                }
            }
        ) {
            Text("Add Car")
        }

        Button(
            onClick = {
                try {
                    navController.navigate(AppNavItem.UserCars.route)
                } catch (e: Exception) {
                    Log.e("Navigation", "Error navigating to UserCars: ${e.message}", e)
                }
            }
        ) {
            Text("View My Cars")
        }
    }
}

package com.example.rentmycar.screens.app

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.rentmycar.viewmodel.MyCarViewModel
import com.example.rentmycar.viewmodel.MyCarViewState
import com.example.rentmycar.api.requests.CarResponse
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import com.example.rentmycar.api.requests.CarDTO
import com.example.rentmycar.navigation.AppNavItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyCarsScreen(navController: NavController) {
    val viewModel: MyCarViewModel = hiltViewModel()
    val viewState = viewModel.viewState.collectAsState().value



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

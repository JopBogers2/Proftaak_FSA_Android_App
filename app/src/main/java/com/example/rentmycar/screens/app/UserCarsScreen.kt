package com.example.rentmycar.screens.app

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.rentmycar.api.requests.CarDTO
import com.example.rentmycar.viewmodel.UserCarsViewModel
import com.example.rentmycar.viewmodel.UserCarsViewState

@Composable
fun UserCarsScreen(navController: NavController) {
    val viewModel: UserCarsViewModel = hiltViewModel()
    val viewState = viewModel.viewState.collectAsState().value

    LaunchedEffect(Unit) {
        viewModel.getUserCars()
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("My Cars", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        when (val state = viewState) {
            is UserCarsViewState.Loading -> CircularProgressIndicator()
            is UserCarsViewState.Success -> UserCarList(state.cars)
            is UserCarsViewState.Error -> Text("Error: ${state.message}")
            is UserCarsViewState.NoCars -> Text("You don't have any cars yet.")
        }
    }
}

@Composable
fun UserCarList(cars: List<CarDTO>) {
    LazyColumn {
        items(cars) { car ->
            UserCarItem(car)
        }
    }
}

@Composable
fun UserCarItem(car: CarDTO) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Model: ${car.model}")
            Text("License Plate: ${car.licensePlate}")
            Text("Year: ${car.year}")
        }
    }
}
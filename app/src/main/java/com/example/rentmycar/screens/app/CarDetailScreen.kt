package com.example.rentmycar.screens.app

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.rentmycar.viewmodel.MyCarViewModel
import com.example.rentmycar.viewmodel.MyCarViewState
import com.example.rentmycar.api.requests.CarResponse
import androidx.compose.material.icons.filled.ArrowBack


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarDetailScreen(carId: Int, viewModel: MyCarViewModel) {
    val viewState = viewModel.viewState.collectAsState().value

/*
    LaunchedEffect(carId) {
        viewModel.getCarById(carId)
    }
*/

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Car Details") },
                navigationIcon = {
                    IconButton(onClick = { /* Handle back navigation */ }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            when (viewState) {
                is MyCarViewState.Loading -> {
                    CircularProgressIndicator()
                }
                is MyCarViewState.Success -> {
                    val car = viewState.cars.firstOrNull()
                    if (car != null) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Model: ${car.model}")
                            Text("License Plate: ${car.licensePlate}")
                            Text("Year: ${car.year}")
                            // Add more car details as needed
                        }
                    } else {
                        Text("Car not found")
                    }
                }
                is MyCarViewState.Error -> {
                    Text("Error: ${viewState.message}")
                }
                else -> {
                    Text("Unexpected state")
                }
            }
        }
    }
}

@Composable
fun CarDetailContent(car: CarResponse) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("ID: ${car.id}")
        Text("Model: ${car.model}")
        Text("License Plate: ${car.licensePlate}")
        Text("Year: ${car.year}")
        Text("Color: ${car.color}")
        Text("Fuel: ${car.fuel}")
        Text("Transmission: ${car.transmission}")
        Text("Price: ${car.price}")
    }
}
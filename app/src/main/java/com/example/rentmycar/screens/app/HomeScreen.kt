package com.example.rentmycar.screens.app

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.rentmycar.api.requests.CarResponse
import com.example.rentmycar.viewmodel.CarViewModel

@Composable
fun HomeScreen(navController: NavController) {
    val carViewModel: CarViewModel = CarViewModel(LocalContext.current)
    var cars by remember { mutableStateOf<List<CarResponse>>(emptyList()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    // Fetch cars from the ViewModel
    LaunchedEffect(Unit) {
        carViewModel.getCars({ carList ->
            cars = carList
            isLoading = false
        }, { error ->
            errorMessage = error
            isLoading = false
        })
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        when {
            isLoading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            errorMessage != null -> {
                Text(
                    text = errorMessage ?: "Unknown error",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            cars.isNotEmpty() -> {
                CarList(cars)
            }
            else -> {
                Text(
                    text = "No cars available.",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}

@Composable
fun CarList(cars: List<CarResponse>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(cars) { car ->
            CarItem(car)
        }
    }
}

@Composable
fun CarItem(car: CarResponse) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = car.model,
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "License Plate: ${car.licensePlate}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Fuel Type: ${car.fuel}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Year: ${car.year}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Color: ${car.color}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Transmission: ${car.transmission}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Price: $${car.price}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Category: ${car.category}",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

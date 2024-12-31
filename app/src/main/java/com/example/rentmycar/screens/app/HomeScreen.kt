package com.example.rentmycar.screens.app

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.rentmycar.api.requests.CarResponse
import com.example.rentmycar.viewmodel.CarViewModel
import com.example.rentmycar.viewmodel.CarViewState

@Composable
fun HomeScreen(navController: NavHostController, viewModel: CarViewModel = hiltViewModel()) {
    AuthenticatedScreen(navController, viewModel.logoutEvent) {

        val viewState by viewModel.viewState.collectAsState()
        LaunchedEffect(Unit) {
            viewModel.getCars()
        }

        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            when (val state = viewState) {
                CarViewState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is CarViewState.Error -> {
                    Text(
                        text = state.message,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is CarViewState.Success -> {
                    CarList(state.cars)
                }
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

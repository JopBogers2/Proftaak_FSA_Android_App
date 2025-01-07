package com.example.rentmycar.screens.app

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyCarsScreen(
    onNavigateToAddCar: () -> Unit,
    onNavigateToCarDetail: (Int) -> Unit,
    viewModel: MyCarViewModel
) {
    val viewState = viewModel.viewState.collectAsState().value

  /*  LaunchedEffect(Unit) {
        viewModel.getUserCars()
    }
*/
    Column(modifier = Modifier.padding(16.dp)) {
        Text("My Cars", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        when (viewState) {
            is MyCarViewState.Loading -> {
                CircularProgressIndicator()
            }
            is MyCarViewState.Success -> {
                LazyColumn {
                    items(viewState.cars) { car ->
                        CarItem(car, onCarClick = { onNavigateToCarDetail(car.id) })
                    }
                }
            }
            is MyCarViewState.NoCars -> {
                Text("You don't have any cars yet.")
            }
            is MyCarViewState.Error -> {
                Text("Error: ${viewState.message}")
            }
            else -> {
                Text("Unexpected state")
            }
        }

        Button(onClick = onNavigateToAddCar) {
            Text("Add Car")
        }
    }
}
@Composable
fun ErrorScreen(message: String) {
    Text("Error: $message", color = Color.Red)
}

@Composable
fun CarList(cars: List<CarResponse>, onCarClick: (Int) -> Unit) {
    Column {
        cars.forEach { car ->
            CarItem(car, onCarClick)
        }
    }
}

@Composable
fun CarItem(car: CarResponse, onCarClick: (Int) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onCarClick(car.id) }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Model: ${car.model}")
            Text("License Plate: ${car.licensePlate}")
            Text("Year: ${car.year}")
        }
    }
}
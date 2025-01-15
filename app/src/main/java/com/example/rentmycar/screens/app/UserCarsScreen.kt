package com.example.rentmycar.screens.app

import android.Manifest
import android.content.pm.PackageManager
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.rentmycar.viewmodel.UserCarsViewModel
import com.example.rentmycar.viewmodel.UserCarsViewState
import com.example.rentmycar.api.requests.CarDTO
import com.example.rentmycar.api.responses.LocationResponse
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.launch

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.lazy.LazyRow
import coil.compose.rememberImagePainter
import com.example.rentmycar.components.ImageCarousel

@Composable
fun UserCarsScreen(navController: NavController, viewModel: UserCarsViewModel = hiltViewModel()) {
    val viewState by viewModel.viewState.collectAsState()
    val locationState by viewModel.locationState.collectAsState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    LaunchedEffect(Unit) {
        viewModel.getUserCars()
    }

    Column {
        Text(
            text = "My Cars",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(16.dp)
        )

        Button(
            onClick = {
                if (hasLocationPermission) {
                    scope.launch {
                        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
                        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                            if (location != null) {
                                viewModel.setLocation(location)
                            }
                        }
                    }
                }
            },
            enabled = hasLocationPermission
        ) {
            Text("Fetch Current Location")
        }

        when (val locState = locationState) {
            is UserCarsViewModel.LocationState.LocationAvailable -> {
                Text("Current Location: ${locState.location.latitude}, ${locState.location.longitude}")
            }
            UserCarsViewModel.LocationState.NoLocation -> {
                Text("Location not available")
            }
        }

        when (val state = viewState) {
            is UserCarsViewState.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier.padding(16.dp)
                )
            }
            is UserCarsViewState.Success -> {
                LazyColumn {
                    items(state.cars) { car ->
                        CarItem(car, viewModel)
                    }
                }
            }
            is UserCarsViewState.NoCars -> {
                Text(
                    text = "You don't have any cars registered.",
                    modifier = Modifier.padding(16.dp)
                )
            }
            is UserCarsViewState.Error -> {
                Text(
                    text = "Error: ${state.message}",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}

@Composable
fun CarItem(car: CarDTO, viewModel: UserCarsViewModel) {
    val context = LocalContext.current
    val carImages by viewModel.carImages.collectAsState()

    LaunchedEffect(car.id) {
        viewModel.getImagesByCar(car.id)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Text(text = "Model: ${car.model}", style = MaterialTheme.typography.titleMedium)
            Text(text = "License Plate: ${car.licensePlate}")
            Text(text = "Year: ${car.year}")
            Text(text = "Color: ${car.color}")
            Text(text = "Fuel: ${car.fuel}")
            Text(text = "Transmission: ${car.transmission}")
            Text(text = "Price: ${car.price}")
            Text(text = "Category: ${car.category}")

            if (car.locationId != null) {
                Text(text = "Location ID: ${car.locationId}")
            } else {
                Text(text = "Location: Not available")
            }

            Spacer(modifier = Modifier.height(8.dp))

            val images = carImages[car.id] ?: emptyList()
            if (images.isNotEmpty()) {
                ImageCarousel(images, context)
            } else {
                Text("No images available")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row {
                Button(
                    onClick = {
                        val currentLocation = (viewModel.locationState.value as? UserCarsViewModel.LocationState.LocationAvailable)?.location
                        if (currentLocation != null) {
                            viewModel.addCarLocation(car.id, currentLocation.latitude, currentLocation.longitude)
                        }
                    },
                    enabled = viewModel.locationState.value is UserCarsViewModel.LocationState.LocationAvailable
                ) {
                    Text("Add Location")
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(onClick = { /* Implement image upload logic */ }) {
                    Text("Upload Image")
                }
            }
        }
    }
}
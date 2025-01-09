package com.example.rentmycar.screens.app

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Alignment
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.rentmycar.api.requests.RegisterCarRequest
import com.example.rentmycar.viewmodel.MyCarViewModel
import com.example.rentmycar.api.requests.BrandDTO


import com.example.rentmycar.api.requests.ModelDTO
import com.example.rentmycar.viewmodel.DataLoadingState
import com.example.rentmycar.viewmodel.RegistrationState

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.tasks.await
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.location.LocationRequest
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.core.content.ContextCompat
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts

import com.example.rentmycar.api.requests.LocationRequest as CustomLocationRequest
import com.google.android.gms.location.LocationRequest as GmsLocationRequest



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCarScreen(
    navController: NavController,
    viewModel: MyCarViewModel = hiltViewModel()
) {
    val registrationState by viewModel.registrationState.collectAsState()
    val brands by viewModel.brands.collectAsState()
    val models by viewModel.models.collectAsState()
    val dataLoadingState by viewModel.dataLoadingState.collectAsState()
    val selectedBrandId by viewModel.selectedBrandId.collectAsState(initial = null)
    val selectedModelId by viewModel.selectedModelId.collectAsState(initial = null)
    val selectedBrand by viewModel.selectedBrand.collectAsState(initial = null)
    val selectedModel by viewModel.selectedModel.collectAsState(initial = null)


    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current



    var errorMessage by remember { mutableStateOf<String?>(null) }
    var fieldErrors by remember { mutableStateOf<Map<String, String>>(emptyMap()) }
    var isCarAdded by remember { mutableStateOf(false) }
    var licensePlate by remember { mutableStateOf("") }
    var year by remember { mutableStateOf("") }
    var color by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var selectedTransmission by remember { mutableStateOf<String?>(null) }
    var selectedFuelType by remember { mutableStateOf<String?>(null) }
    var expandedBrand by remember { mutableStateOf(false) }
    var expandedModel by remember { mutableStateOf(false) }
    var expandedTransmission by remember { mutableStateOf(false) }
    var expandedFuelType by remember { mutableStateOf(false) }

    var currentLocation by remember { mutableStateOf<Location?>(null) }
    var locationText by remember { mutableStateOf<String?>(null) }
    var location by remember { mutableStateOf<Location?>(null) }
    val transmissionOptions = listOf("AUTOMATIC", "MANUAL")
    val fuelTypeOptions = listOf("DIESEL", "PETROL", "GAS", "ELECTRIC", "HYDROGEN")

     var hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        hasLocationPermission = isGranted
    }

LaunchedEffect(registrationState) {
    Log.d("AddCarScreen", "Registration state changed: $registrationState")
    when (registrationState) {
        is RegistrationState.Success -> {
            Log.d("AddCarScreen", "Car added successfully")
            isCarAdded = true
            // Clear form fields
            licensePlate = ""
            year = ""
            color = ""
            price = ""
            selectedTransmission = null
            selectedFuelType = null
            viewModel.selectBrand(-1)
            viewModel.selectModel(-1)
            // Show success message
            errorMessage = "Car added successfully!"
        }
        is RegistrationState.Error -> {
            Log.e("AddCarScreen", "Error adding car: ${(registrationState as RegistrationState.Error).message}")
            errorMessage = (registrationState as RegistrationState.Error).message
            fieldErrors = (registrationState as RegistrationState.Error).fieldErrors
        }
        is RegistrationState.Loading -> {
            Log.d("AddCarScreen", "Car registration in progress")
        }
        else -> {
            Log.d("AddCarScreen", "Unknown registration state: $registrationState")
        }
    }
}


    LaunchedEffect(hasLocationPermission) {
        if (!hasLocationPermission) {
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }


    LaunchedEffect(Unit) {
        viewModel.fetchBrands()
    }

    LaunchedEffect(selectedBrandId) {
        selectedBrandId?.let { viewModel.fetchModels(it) }
    }

    LaunchedEffect(dataLoadingState) {
        when (val state = dataLoadingState) {
            is DataLoadingState.Error -> {
                errorMessage = state.message
            }
            is DataLoadingState.Success -> {
                errorMessage = null
            }
            else -> {
                // Handle other states if needed
            }
        }
    }

    Column(modifier = Modifier.padding(16.dp)) {
        when (dataLoadingState) {
            is DataLoadingState.Loading -> {
                CircularProgressIndicator()
            }
            is DataLoadingState.Success -> {


                 LocationSection(
                    hasLocationPermission = hasLocationPermission,
                    onFetchLocation = {
                        coroutineScope.launch {
                            currentLocation = getCurrentLocation(context)
                            locationText = if (currentLocation != null) {
                                "Location fetched: ${currentLocation!!.latitude}, ${currentLocation!!.longitude}"
                            } else {
                                "Failed to fetch location"
                            }
                        }
                    },
                    locationText = locationText
                )



                // Brand dropdown
                ExposedDropdownMenuBox(
                    expanded = expandedBrand,
                    onExpandedChange = { expandedBrand = !expandedBrand }
                ) {
                    TextField(
                        value = selectedBrand?.name ?: "",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Brand") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedBrand) },
                        colors = ExposedDropdownMenuDefaults.textFieldColors(),
                        modifier = Modifier.menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = expandedBrand,
                        onDismissRequest = { expandedBrand = false }
                    ) {
                        brands.forEach { brand ->
                            DropdownMenuItem(
                                text = { Text(brand.name) },
                                onClick = {
                                    viewModel.selectBrand(brand.id)
                                    expandedBrand = false
                                }
                            )
                        }
                    }
                }

                // Model dropdown
                if (selectedBrandId != null) {
                    ExposedDropdownMenuBox(
                        expanded = expandedModel,
                        onExpandedChange = { expandedModel = !expandedModel }
                    ) {
                        TextField(
                            value = selectedModel?.name ?: "",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Model") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedModel) },
                            colors = ExposedDropdownMenuDefaults.textFieldColors(),
                            modifier = Modifier.menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = expandedModel,
                            onDismissRequest = { expandedModel = false }
                        ) {
                            models.forEach { model ->
                                DropdownMenuItem(
                                    text = { Text(model.name) },
                                    onClick = {
                                        viewModel.selectModel(model.id)
                                        expandedModel = false
                                    }
                                )
                            }
                        }
                    }
                }

                // License Plate
                CarTextField(
                    value = licensePlate,
                    onValueChange = { licensePlate = it },
                    label = "License Plate",
                    error = fieldErrors["licensePlate"]
                )

                // Year
                CarTextField(
                    value = year,
                    onValueChange = { year = it },
                    label = "Year",
                    error = fieldErrors["year"]
                )

                // Color
                CarTextField(
                    value = color,
                    onValueChange = { color = it },
                    label = "Color",
                    error = fieldErrors["color"]
                )

                // Price
                CarTextField(
                    value = price,
                    onValueChange = { price = it },
                    label = "Price",
                    error = fieldErrors["price"]
                )

                // Transmission dropdown
                ExposedDropdownMenuBox(
                    expanded = expandedTransmission,
                    onExpandedChange = { expandedTransmission = !expandedTransmission }
                ) {
                    TextField(
                        value = selectedTransmission ?: "",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Transmission") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedTransmission) },
                        colors = ExposedDropdownMenuDefaults.textFieldColors(),
                        modifier = Modifier.menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = expandedTransmission,
                        onDismissRequest = { expandedTransmission = false }
                    ) {
                        transmissionOptions.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    selectedTransmission = option
                                    expandedTransmission = false
                                }
                            )
                        }
                    }
                }

                // Fuel Type dropdown
                ExposedDropdownMenuBox(
                    expanded = expandedFuelType,
                    onExpandedChange = { expandedFuelType = !expandedFuelType }
                ) {
                    TextField(
                        value = selectedFuelType ?: "",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Fuel Type") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedFuelType) },
                        colors = ExposedDropdownMenuDefaults.textFieldColors(),
                        modifier = Modifier.menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = expandedFuelType,
                        onDismissRequest = { expandedFuelType = false }
                    ) {
                        fuelTypeOptions.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    selectedFuelType = option
                                    expandedFuelType = false
                                }
                            )
                        }
                    }
                }



Button(
    onClick = {
        coroutineScope.launch {
            val carRequest = RegisterCarRequest(
                licensePlate = licensePlate,
                modelId = selectedModelId ?: 0,
                fuel = selectedFuelType ?: "",
                year = year.toIntOrNull() ?: 0,
                color = color,
                transmission = selectedTransmission ?: "",
                price = price.toDoubleOrNull() ?: 0.0
            )

            viewModel.registerCar(carRequest) { carId ->
                if (carId != null) {
                    // Car registration successful
                    if (currentLocation != null) {
                        // If we have location data, send a separate request to add the location
                        val locationRequest = CustomLocationRequest(
                            carId = carId,
                            latitude = currentLocation!!.latitude,
                            longitude = currentLocation!!.longitude
                        )
                        viewModel.addLocation(locationRequest)
                    } else {
                        // Try to fetch location if not available
                        coroutineScope.launch {
                            currentLocation = getCurrentLocation(context)
                            if (currentLocation != null) {
                                val locationRequest = CustomLocationRequest(
                                    carId = carId,
                                    latitude = currentLocation!!.latitude,
                                    longitude = currentLocation!!.longitude
                                )
                                viewModel.addLocation(locationRequest)
                            } else {
                                // Car registered successfully without location
                                errorMessage = "Car registered successfully, but no location was set!"
                            }
                        }
                    }
                } else {
                    // Car registration failed
                    errorMessage = "Failed to register car"
                }
            }
        }
    },
    modifier = Modifier.align(Alignment.CenterHorizontally)
) {
    Text("Add Car")
}

                if (isCarAdded) {
                    Text(
                        "Car added successfully!",
                        color = Color.Green,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                }

                if (errorMessage != null) {
                    Text(
                        errorMessage!!,
                        color = if (isCarAdded) Color.Green else Color.Red,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                }
            }
            else -> {
                // Handle initial state if needed
            }
        }

        if (registrationState is RegistrationState.Loading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        }
    }
}



@Composable
fun LocationSection(
    hasLocationPermission: Boolean,
    onFetchLocation: () -> Unit,
    locationText: String?
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Button(
            onClick = onFetchLocation,
            enabled = hasLocationPermission,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Fetch Current Location")
        }
        if (locationText != null) {
            Text(
                text = locationText,
                color = Color.Blue,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}




suspend fun getCurrentLocation(context: Context): Location? {
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    if (ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        return null
    }

    return try {
        fusedLocationClient.getCurrentLocation(GmsLocationRequest.PRIORITY_HIGH_ACCURACY, null).await()
    } catch (e: Exception) {
        null
    }
}

@Composable
fun CarTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    error: String?
) {
    Column {
        TextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label) },
            isError = error != null,
            modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }
    }




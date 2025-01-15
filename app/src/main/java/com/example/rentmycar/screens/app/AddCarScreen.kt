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
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.rentmycar.api.requests.RegisterCarRequest
import com.example.rentmycar.viewmodel.MyCarViewModel
import com.example.rentmycar.api.requests.BrandDTO
import com.example.rentmycar.api.requests.CarResponse
import com.example.rentmycar.api.requests.ModelDTO
import com.example.rentmycar.viewmodel.DataLoadingState
import com.example.rentmycar.viewmodel.RegistrationState




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
    val transmissionOptions = listOf("AUTOMATIC", "MANUAL")
    val fuelTypeOptions = listOf("DIESEL", "PETROL", "GAS", "ELECTRIC", "HYDROGEN")





LaunchedEffect(registrationState) {
    Log.d("AddCarScreen", "Registration state changed: $registrationState")
    when (registrationState) {
        is RegistrationState.Success -> {

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


        }
        is RegistrationState.Error -> {
            Log.e("AddCarScreen", "Error adding car: ${(registrationState as RegistrationState.Error).message}")
            isCarAdded = false
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

            }
        }
    }

    Column(modifier = Modifier.padding(16.dp)) {
        when (dataLoadingState) {
            is DataLoadingState.Loading -> {
                CircularProgressIndicator()
            }
            is DataLoadingState.Success -> {



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


                CarTextField(
                    value = licensePlate,
                    onValueChange = { licensePlate = it },
                    label = "License Plate",
                    error = fieldErrors["licensePlate"]
                )


                CarTextField(
                    value = year,
                    onValueChange = { year = it },
                    label = "Year",
                    error = fieldErrors["year"]
                )

                CarTextField(
                    value = color,
                    onValueChange = { color = it },
                    label = "Color",
                    error = fieldErrors["color"]
                )

                CarTextField(
                    value = price,
                    onValueChange = { price = it },
                    label = "Price",
                    error = fieldErrors["price"]
                )


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
                isCarAdded = true
                errorMessage = "Car added successfully with ID: $carId"

                licensePlate = ""
                year = ""
                color = ""
                price = ""
                selectedTransmission = null
                selectedFuelType = null
                viewModel.selectBrand(-1)
                viewModel.selectModel(-1)
            } else {
                isCarAdded = false
                errorMessage = "Failed to add car. Please try again."
            }
        }
    },
    modifier = Modifier.align(Alignment.CenterHorizontally)
) {
    Text("Add Car")
}



if (errorMessage != null) {
    Text(
        errorMessage!!,
        color = if (isCarAdded) Color.Green else Color.Red,
        modifier = Modifier.padding(top = 16.dp)
    )
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
                
            }
        }

        if (registrationState is RegistrationState.Loading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        }
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




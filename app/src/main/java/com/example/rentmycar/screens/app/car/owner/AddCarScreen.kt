package com.example.rentmycar.screens.app.car.owner

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.rentmycar.api.requests.RegisterCarRequest
import com.example.rentmycar.viewmodel.car.owner.DataLoadingState
import com.example.rentmycar.viewmodel.car.owner.OwnedCarViewModel
import com.example.rentmycar.viewmodel.car.owner.RegistrationState
import kotlinx.coroutines.delay

@Composable
fun AddCarScreen(
    viewModel: OwnedCarViewModel = hiltViewModel(),
    onCarAdded: () -> Unit = {},
    onDismiss: () -> Unit = {}
) {
    val registrationState by viewModel.registrationState.collectAsState()
    val brands by viewModel.brands.collectAsState()
    val models by viewModel.models.collectAsState()
    val dataLoadingState by viewModel.dataLoadingState.collectAsState()
    val selectedBrandId by viewModel.selectedBrandId.collectAsState(initial = null)
    val selectedModelId by viewModel.selectedModelId.collectAsState(initial = null)
    val selectedBrand by viewModel.selectedBrand.collectAsState(initial = null)
    val selectedModel by viewModel.selectedModel.collectAsState(initial = null)
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
                errorMessage = "Car added successfully!"
                onCarAdded()
                delay(2000)
                onDismiss()
            }

            is RegistrationState.Error -> {
                Log.e(
                    "AddCarScreen",
                    "Error adding car: ${(registrationState as RegistrationState.Error).message}"
                )
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
                RoundedDropdownMenu(
                    expanded = expandedBrand,
                    onExpandedChange = { expandedBrand = it },
                    value = selectedBrand?.name ?: "",
                    label = "Brand",
                    options = brands.map { it.name },
                    onOptionSelected = { brandName ->
                        val brand = brands.find { it.name == brandName }
                        brand?.let {
                            viewModel.selectBrand(it.id)
                            expandedBrand = false
                        }
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))


                RoundedDropdownMenu(
                    expanded = expandedModel,
                    onExpandedChange = { expandedModel = !expandedModel },
                    value = selectedModel?.name ?: "",
                    label = "Model",
                    options = models.map { it.name },
                    onOptionSelected = { modelName ->
                        val model = models.find { it.name == modelName }
                        model?.let { viewModel.selectModel(it.id) }
                        expandedModel = false
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))


                RoundedTextField(
                    value = licensePlate,
                    onValueChange = { licensePlate = it },
                    label = "License Plate"
                )

                Spacer(modifier = Modifier.height(8.dp))


                RoundedTextField(
                    value = year,
                    onValueChange = { year = it },
                    label = "Year"
                )

                Spacer(modifier = Modifier.height(8.dp))


                RoundedTextField(
                    value = color,
                    onValueChange = { color = it },
                    label = "Color"
                )

                Spacer(modifier = Modifier.height(8.dp))


                RoundedTextField(
                    value = price,
                    onValueChange = { price = it },
                    label = "Price"
                )

                Spacer(modifier = Modifier.height(8.dp))


                RoundedDropdownMenu(
                    expanded = expandedTransmission,
                    onExpandedChange = { expandedTransmission = !expandedTransmission },
                    value = selectedTransmission ?: "",
                    label = "Transmission",
                    options = transmissionOptions,
                    onOptionSelected = {
                        selectedTransmission = it
                        expandedTransmission = false
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))


                RoundedDropdownMenu(
                    expanded = expandedFuelType,
                    onExpandedChange = { expandedFuelType = !expandedFuelType },
                    value = selectedFuelType ?: "",
                    label = "Fuel Type",
                    options = fuelTypeOptions,
                    onOptionSelected = {
                        selectedFuelType = it
                        expandedFuelType = false
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))

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

                if (isCarAdded) {
                    Text(
                        "Car added successfully!",
                        color = Color.Green,
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
fun RoundedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoundedDropdownMenu(
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    value: String,
    label: String,
    options: List<String>,
    onOptionSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(expanded) }

    ExposedDropdownMenuBox(
        expanded = isExpanded,
        onExpandedChange = {
            isExpanded = it
            onExpandedChange(it)
        },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded) },
            modifier = Modifier
                .fillMaxWidth(),
            shape = RoundedCornerShape(8.dp)
        )
        ExposedDropdownMenu(
            expanded = isExpanded,
            onDismissRequest = {
                isExpanded = false
                onExpandedChange(false)
            }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onOptionSelected(option)
                        isExpanded = false
                        onExpandedChange(false)
                    }
                )
            }
        }
    }
}
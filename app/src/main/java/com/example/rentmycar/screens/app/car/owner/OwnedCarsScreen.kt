package com.example.rentmycar.screens.app.car.owner

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.rentmycar.R
import com.example.rentmycar.api.responses.OwnedCarResponse
import com.example.rentmycar.components.ExpandableCard
import com.example.rentmycar.components.ImageCarousel
import com.example.rentmycar.viewmodel.car.CarUpdateViewModel
import com.example.rentmycar.viewmodel.car.UpdateState
import com.example.rentmycar.viewmodel.car.owner.OwnedCarViewModel
import com.example.rentmycar.viewmodel.car.owner.OwnedCarsViewModel
import com.example.rentmycar.viewmodel.car.owner.UserCarsViewState
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@Composable
fun OwnedCarsScreen(navController: NavController, viewModel: OwnedCarsViewModel = hiltViewModel()) {
    val viewState by viewModel.viewState.collectAsState()
    var showAddCarDialog by remember { mutableStateOf(false) }
    var refreshTrigger by remember { mutableIntStateOf(0) }

      LaunchedEffect(refreshTrigger) {
        viewModel.getUserCars()
    }

    LaunchedEffect(Unit) {
        viewModel.getUserCars()
    }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxHeight(),
        verticalArrangement = Arrangement.Top,
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
        ) {

            Text(
                "My Cars",
                style = MaterialTheme.typography.headlineMedium,
            )


            Button(onClick = { showAddCarDialog = true }) {
                Icon(
                    painter = painterResource(R.drawable.add),
                    contentDescription = "Add icon",
                    modifier = Modifier.padding(end = 4.dp),
                )
                Text("Add Car")
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
                        CarItem(navController, car, viewModel)
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
    if (showAddCarDialog) {
        AddCarDialog(
            onDismiss = { showAddCarDialog = false },
            onCarAdded = {
                showAddCarDialog = false
                refreshTrigger++
            }
        )
    }
}


@Composable
fun AddCarDialog(onDismiss: () -> Unit, onCarAdded: () -> Unit) {
    val addCarViewModel: OwnedCarViewModel = hiltViewModel()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add New Car") },
        text = {
            AddCarScreen(
                viewModel = addCarViewModel,
                onCarAdded = {
                    onCarAdded()
                    onDismiss()
                }
            )
        },
        confirmButton = {},
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun CarItem(navController: NavController, car: OwnedCarResponse, viewModel: OwnedCarsViewModel) {
    val context = LocalContext.current
    val carImages by viewModel.carImages.collectAsState()
    val carUpdateViewModel: CarUpdateViewModel = hiltViewModel()

    var tempImageUri by remember { mutableStateOf<Uri?>(null) }
    var showDialog by remember { mutableStateOf(false) }
    var isUploading by remember { mutableStateOf(false) }



    LaunchedEffect(car.id) {
        viewModel.getImagesByCar(car.id)
    }

    LaunchedEffect(isUploading) {
        if (!isUploading) {
            viewModel.getImagesByCar(car.id)
        }
    }

    val galleryLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                isUploading = true
                viewModel.uploadCarImage(car.id, it) { success ->
                    if (success) {
                        isUploading = false
                    }
                }
            }
        }

    val cameraLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) {
                tempImageUri?.let { uri ->
                    isUploading = true
                    viewModel.uploadCarImage(car.id, uri) { uploadSuccess ->
                        if (uploadSuccess) {
                            isUploading = false
                        }
                    }
                }
            }
        }



    OutlinedCard(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
    ) {
        Column(
            modifier = Modifier
                .padding(8.dp)
        ) {

              Text("Brand: ${car.brand}", style = MaterialTheme.typography.titleMedium)
            Text(" ${car.model}", style = MaterialTheme.typography.titleMedium)

            Spacer(modifier = Modifier.height(8.dp))

            if (car.locationId != null) {
                Text(text = "Location: Location set")
            } else {
                Text(text = "Location: No Location set")
            }

            ExpandableCard(title = "Images") {
                Column {
                    val images = carImages[car.id] ?: emptyList()
                    if (images.isNotEmpty()) {
                        ImageCarousel(images, context)
                    } else {
                        Text("No images available")
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))


          CarInfo(car, carUpdateViewModel)

            if (car.locationId != null) {
                Text(text = "Location ID: ${car.locationId}")
            } else {
                Text(text = "Location: Not available")
            }

            ExpandableCard(title = "Management options") {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Button(
                        onClick = {
                            Log.d("UserCarsScreen", "Add Location button clicked for car ${car.id}")
                            viewModel.addCarLocation(car.id)
                        },
                        enabled = true
                    ) {
                        Text("Set Location")
                    }

                    Button(onClick = { showDialog = true }) {
                        Text("Upload photo")
                    }

                    Button(onClick = {
                        navController.navigate("timeslotManagement/${car.id}")
                    }) {
                        Text("Timeslots")
                    }
                }

            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                colors = ButtonDefaults.buttonColors().copy(
                    containerColor = Color.Red
                ), onClick = { viewModel.unregisterCar(car.id) }) {
                Text(text = "Remove car")
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Upload Image") },
            text = { Text("Choose an option") },
            confirmButton = {
                Button(onClick = {
                    showDialog = false
                    val timeStamp: String =
                        SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
                    val storageDir: File? = context.getExternalFilesDir(null)
                    val tempFile = File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir)
                    val uri = FileProvider.getUriForFile(
                        context,
                        "${context.packageName}.fileprovider",
                        tempFile
                    )
                    tempImageUri = uri
                    cameraLauncher.launch(uri)
                }) {
                    Text("Take Photo")
                }
            },
            dismissButton = {
                Button(onClick = {
                    showDialog = false
                    galleryLauncher.launch("image/*")
                }) {
                    Text("Gallery")
                }
            }
        )
    }

}



@Composable
fun CarInfo(car: OwnedCarResponse, carUpdateViewModel: CarUpdateViewModel = hiltViewModel()) {
    var editingField by remember { mutableStateOf<String?>(null) }
    var editedCar by remember(car) { mutableStateOf(car) }

    val updateState by carUpdateViewModel.updateState.collectAsState()
    val carDetailsMap by carUpdateViewModel.carDetailsMap.collectAsState()

    LaunchedEffect(car.id) {
        carUpdateViewModel.getCarDetails(car.id)
    }

    Column(modifier = Modifier.padding(16.dp)) {
        val currentCar = carDetailsMap[car.id] ?: car


        Text("Category: ${currentCar.category}",
             style = MaterialTheme.typography.bodyMedium,
             modifier = Modifier.padding(vertical = 4.dp))

        EditableDropdownField(
            label = "Fuel",
            value = currentCar.fuel,
            options = listOf("DIESEL", "PETROL", "GAS", "ELECTRIC", "HYDROGEN"),
            isEditing = editingField == "fuel"
        ) { newValue ->
            editedCar = editedCar.copy(fuel = newValue)
            editingField = null
            carUpdateViewModel.updateCar(editedCar)
        }
        EditableDropdownField(
            label = "Transmission",
            value = currentCar.transmission,
            options = listOf("AUTOMATIC", "MANUAL"),
            isEditing = editingField == "transmission"
        ) { newValue ->
            editedCar = editedCar.copy(transmission = newValue)
            editingField = null
            carUpdateViewModel.updateCar(editedCar)
        }
        EditableField("Color", currentCar.color, editingField == "color") { newValue ->
            editedCar = editedCar.copy(color = newValue)
            editingField = null
            carUpdateViewModel.updateCar(editedCar)
        }

            EditableField("Price", currentCar.price.toString(), editingField == "price") { newValue ->
            val newPrice = newValue.toDoubleOrNull()
            if (newPrice != null) {
                editedCar = editedCar.copy(price = newPrice)
                editingField = null
                carUpdateViewModel.updateCar(editedCar)
               }
           }

           EditableField("Year", currentCar.year.toString(), editingField == "year") { newValue ->
            val newYear = newValue.toIntOrNull()
            if (newYear != null) {
                editedCar = editedCar.copy(year = newYear)
                editingField = null
                carUpdateViewModel.updateCar(editedCar)
            }
        }

        Text("License plate: ${currentCar.licensePlate}",
             style = MaterialTheme.typography.bodyMedium,
             modifier = Modifier.padding(vertical = 4.dp))

        when (updateState) {
            is UpdateState.Loading -> {
                CircularProgressIndicator(modifier = Modifier.padding(8.dp))
            }
            is UpdateState.Success -> {
                Text(
                    text = (updateState as UpdateState.Success).message,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(8.dp)
                )
            }
            is UpdateState.Error -> {
                val errorState = updateState as UpdateState.Error
                Text(
                    text = "Error: ${errorState.message}",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(8.dp)
                )
                errorState.fieldErrors.forEach { (field, error) ->
                    Text(
                        text = "$field: $error",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                    )
                }
            }
            else -> {}
        }
    }
}

@Composable
fun EditableField(
    label: String,
    value: String,
    isEditing: Boolean,
    onValueChange: (String) -> Unit
) {
    var text by remember { mutableStateOf(value) }
    var editing by remember { mutableStateOf(isEditing) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (editing) {
            TextField(
                value = text,
                onValueChange = { text = it },
                label = { Text(label) },
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp),
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        editing = false
                        onValueChange(text)
                    }
                )
            )
            Button(onClick = {
                editing = false
                onValueChange(text)
            }) {
                Text("Save")
            }
        } else {
            Text(
                text = "$label: $value",
                modifier = Modifier
                    .weight(1f)
                    .clickable { editing = true }
            )
            IconButton(onClick = { editing = true }) {
                Icon(Icons.Default.Edit, contentDescription = "Edit")
            }
        }
    }
}

@Composable
fun EditableDropdownField(
    label: String,
    value: String,
    options: List<String>,
    isEditing: Boolean,
    onValueChange: (String) -> Unit
) {
    var editing by remember { mutableStateOf(isEditing) }
    var expanded by remember { mutableStateOf(false) }
    var selectedOption by remember { mutableStateOf(value) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (editing) {
            Box(modifier = Modifier.weight(1f)) {
                OutlinedButton(
                    onClick = { expanded = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(selectedOption)
                }
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    options.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                selectedOption = option
                                expanded = false
                            }
                        )
                    }
                }
            }
            Button(onClick = {
                editing = false
                onValueChange(selectedOption)
            }) {
                Text("Save")
            }
        } else {
            Text(
                text = "$label: $value",
                modifier = Modifier
                    .weight(1f)
                    .clickable { editing = true }
            )
            IconButton(onClick = { editing = true }) {
                Icon(Icons.Default.Edit, contentDescription = "Edit")
            }
        }
    }
}
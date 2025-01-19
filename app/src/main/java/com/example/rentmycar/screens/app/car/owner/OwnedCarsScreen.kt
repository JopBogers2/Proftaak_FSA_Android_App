package com.example.rentmycar.screens.app.car.owner

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.rentmycar.R
import com.example.rentmycar.api.responses.OwnedCarResponse
import com.example.rentmycar.components.ExpandableCard
import com.example.rentmycar.components.ImageCarousel
import com.example.rentmycar.components.car.SpecificationRow
import com.example.rentmycar.navigation.AppNavItem
import com.example.rentmycar.viewmodel.car.owner.OwnedCarsViewModel
import com.example.rentmycar.viewmodel.car.owner.UserCarsViewState
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun OwnedCarsScreen(navController: NavController, viewModel: OwnedCarsViewModel = hiltViewModel()) {
    val viewState by viewModel.viewState.collectAsState()

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
            // Title
            Text(
                "My Cars",
                style = MaterialTheme.typography.headlineMedium,
            )

            Button(onClick = {
                try {
                    navController.navigate(AppNavItem.AddCar.route)
                } catch (e: Exception) {
                    Log.e("Navigation", "Error navigating to AddCar: ${e.message}", e)
                }
            }) {
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
}

@Composable
fun CarItem(navController: NavController, car: OwnedCarResponse, viewModel: OwnedCarsViewModel) {
    val context = LocalContext.current
    val carImages by viewModel.carImages.collectAsState()

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
            Text("Model: ${car.model}", style = MaterialTheme.typography.titleMedium)

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

            ExpandableCard(title = "Car details") {
                Column(modifier = Modifier.padding(16.dp)) {
                    CarInfo(car)
                }
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
fun CarInfo(car: OwnedCarResponse) {
    Column(modifier = Modifier.padding(4.dp)) {
        SpecificationRow("Model", car.model)
        SpecificationRow("Category", car.category)
        SpecificationRow("Fuel", car.fuel)
        SpecificationRow("Transmission", car.transmission)
        SpecificationRow("Color", car.color)
        SpecificationRow("License plate", car.licensePlate)
    }
}
package com.example.rentmycar.screens.app.car

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.rentmycar.R
import com.example.rentmycar.api.responses.CarLocationResponse
import com.example.rentmycar.api.responses.CarResponse
import com.example.rentmycar.components.ExpandableCard
import com.example.rentmycar.components.ImageCarousel
import com.example.rentmycar.components.car.SpecificationRow
import com.example.rentmycar.screens.app.AuthenticatedScreen
import com.example.rentmycar.utils.helpers.LocationHelper
import com.example.rentmycar.utils.helpers.RedirectHelper
import com.example.rentmycar.viewmodel.car.CarViewModel
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState

@Composable
fun CarItemScreen(
    navController: NavHostController,
    carId: Int,
) {
    val carViewModel = hiltViewModel<CarViewModel>()

    AuthenticatedScreen(navController, carViewModel.logoutEvent) {
        // Initialize the car on page enter
        LaunchedEffect(Unit) {
            carViewModel.initCar(carId)
        }

        val context = LocalContext.current

        val car = carViewModel.car.observeAsState().value ?: return@AuthenticatedScreen
        val imagePaths by carViewModel.images.observeAsState(emptyList())
        val carLocation by carViewModel.location.observeAsState()

        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState(0)),
            verticalArrangement = Arrangement.Top,
        ) {
            // Button to navigate back to home screen
            TextButton(
                onClick = {
                    navController.navigate("home")
                },
                contentPadding = PaddingValues(all = 0.dp),
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    contentDescription = stringResource(R.string.back_icon),
                )
                Text(stringResource(R.string.back_home))
            }

            // Header
            CarItemHeader(car)

            // Images carousel
            Row(horizontalArrangement = Arrangement.Center) {
                if (imagePaths.isNotEmpty()) {
                    ImageCarousel(imagePaths.toList(), context)
                }
            }

            // Section with action buttons
            CarItemActions(car, context, navController)

            // Expanded section with the location of the car
            CarItemLocation(carLocation, context, car)

            // Expanded section with additional info about car
            CarItemInfo(car)
        }
    }
}

/**
 * Header of the page which contains the general info about the car
 */
@Composable
fun CarItemHeader(car: CarResponse) {
    Row(
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .padding(top = 10.dp)
            .fillMaxWidth()
    ) {
        Column {
            // Brand & model
            Text(
                "${car.brand}, ${car.model}",
                style = MaterialTheme.typography.headlineMedium
            )
            // Owner
            Text(stringResource(R.string.owner, car.ownerName))
        }

        // Price
        Text("$${car.price}")
    }
}

/**
 * Section of the page which contains Reserve & Contact action buttons
 */
@Composable
fun CarItemActions(car: CarResponse, context: Context, navController: NavHostController) {
    val redirectHelper = RedirectHelper()

    Row(
        modifier = Modifier.fillMaxWidth(),
    ) {
        // Button to reserve the car.
        Button(
            onClick = {
                navController.navigate("reservableTimeslots/${car.id}")
            },
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
                .padding(end = 4.dp),
        ) {
            Icon(
                imageVector = Icons.Default.DateRange,
                contentDescription = stringResource(R.string.reserve_icon),
            )
            Text(stringResource(R.string.reserve_now))
        }

        // Button which opens the mail app with owner's email in "To" text field
        OutlinedButton(
            onClick = {
                redirectHelper.redirectToEmail(
                    userEmail = car.ownerEmail,
                    context = context,
                    onError = {
                        Toast.makeText(context,
                            context.getString(R.string.no_email_app_found), Toast.LENGTH_SHORT).show()
                    }
                )
            },
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
                .padding(start = 4.dp),
        ) {
            Icon(
                imageVector = Icons.Default.Email,
                contentDescription = stringResource(R.string.email_icon),
            )
            Text(stringResource(R.string.contact_owner))
        }
    }
}

/**
 * Expandable section of the page which contains the location of the car
 */
@Composable
fun CarItemLocation(carLocation: CarLocationResponse?, context: Context, car: CarResponse) {
    val locationHelper = LocationHelper(context)
    val hasLocationPermissions = rememberSaveable {
        locationHelper.hasLocationPermissions()
    }

    if (carLocation != null && hasLocationPermissions) {
        // Get car latitude & longitude
        val carLatLong = LatLng(
            carLocation.latitude,
            carLocation.longitude,
        )
        // Set the marker state
        val carMarkerState = rememberMarkerState(position = carLatLong)

        // Set the camera position
        val cameraPosition = rememberCameraPositionState {
            position = CameraPosition.fromLatLngZoom(carLatLong, 4f)
        }

        // Display the location
        ExpandableCard(title = "Location") {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
                    .padding(16.dp)
            ) {
                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraPosition,
                ) {
                    Marker(
                        state = carMarkerState,
                        title = stringResource(R.string.by, car.brand, car.model, car.ownerName),
                        icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE),
                    )
                }
            }
        }
    }
}

/**
 * Expandable section of the page which contains the additional info about the car
 */
@Composable
fun CarItemInfo(car: CarResponse) {
    ExpandableCard(title = "Specifications") {
        Column(modifier = Modifier.padding(16.dp)) {
            SpecificationRow(stringResource(R.string.brand), car.brand)
            SpecificationRow(stringResource(R.string.model), car.model)
            SpecificationRow(stringResource(R.string.category), car.category)
            SpecificationRow(stringResource(R.string.fuel), car.fuel)
            SpecificationRow(stringResource(R.string.transmission), car.transmission)
            SpecificationRow(stringResource(R.string.color), car.color)
            SpecificationRow(stringResource(R.string.license_plate), car.licensePlate)
        }
    }
}

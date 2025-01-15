package com.example.rentmycar.screens.app

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.rentmycar.viewmodel.DirectionsViewModel
import com.example.rentmycar.viewmodel.DirectionsViewState
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState

@Composable
fun DirectionScreen(
    navController: NavHostController,
    carId: Int
) {
    val viewModel = hiltViewModel<DirectionsViewModel>()

    AuthenticatedScreen(navController, viewModel.logoutEvent) {
        val viewState by viewModel.viewState.collectAsState()

        LaunchedEffect(Unit) {
            viewModel.getDirections(carId)
        }

        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            when (val state = viewState) {
                DirectionsViewState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                is DirectionsViewState.Error -> {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxHeight()
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.Top,
                    ) {
                        Text(
                            "Directions",
                            style = MaterialTheme.typography.headlineMedium,
                        )
                        TextButton(
                            onClick = {
                                navController.navigate("reservations")
                            },
                            contentPadding = PaddingValues(all = 0.dp),
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                                contentDescription = "Back icon",
                            )
                            Text("Back to reservations")
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = state.message,
                                style = MaterialTheme.typography.bodyLarge,
                            )
                        }
                    }
                }

                is DirectionsViewState.Success -> {
                    val startingPosition = LatLng(
                        state.directions[0].routes[0].legs[0].steps.first().startLocation.lat,
                        state.directions[0].routes[0].legs[0].steps.first().startLocation.lng,
                    )

                    val destinationPosition = LatLng(
                        state.directions[0].routes[0].legs[0].steps.last().startLocation.lat,
                        state.directions[0].routes[0].legs[0].steps.last().startLocation.lng,
                    )

                    val cameraPositionState = rememberCameraPositionState {
                        position = CameraPosition.fromLatLngZoom(startingPosition, 10f)
                    }
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxHeight(),
                        verticalArrangement = Arrangement.Top,
                    ) {
                        Text(
                            "Directions",
                            style = MaterialTheme.typography.headlineMedium,
                        )
                        // Button to navigate back to reservation screen
                        TextButton(
                            onClick = {
                                navController.navigate("reservations")
                            },
                            contentPadding = PaddingValues(all = 0.dp),
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                                contentDescription = "Back icon",
                            )
                            Text("Back to reservations")
                        }

                        GoogleMap(
                            cameraPositionState = cameraPositionState
                        ) {
                            // Set the marker state
                            val userLocationMarkerState = rememberMarkerState(
                                position = startingPosition
                            )

                            val carLocationMarkerState = rememberMarkerState(
                                position = destinationPosition
                            )

                            // Draw the polyline for directions
                            val points = state.directions[0].routes[0].legs[0].steps.map { step ->
                                LatLng(step.startLocation.lat, step.startLocation.lng)
                            }

                            Polyline(
                                points = points,
                                color = Color.Blue,
                                width = 10f
                            )
                            Marker(
                                // user location marker.
                                state = userLocationMarkerState,
                                icon = BitmapDescriptorFactory
                                    .defaultMarker(BitmapDescriptorFactory.HUE_BLUE),
                            )
                            Marker(
                                // destination marker.
                                state = carLocationMarkerState,
                                icon = BitmapDescriptorFactory
                                    .defaultMarker(BitmapDescriptorFactory.HUE_ROSE),
                            )
                        }
                    }
                }
            }
        }
    }
}
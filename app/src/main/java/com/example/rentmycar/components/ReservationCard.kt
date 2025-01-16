package com.example.rentmycar.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.rentmycar.api.responses.TimeslotResponse
import com.example.rentmycar.viewmodel.ReservationsViewModel

/**
 * Card which contains general info about the car without details or location.
 */
@Composable
fun ReservationCard(reservedTimeslot: TimeslotResponse, navController: NavController) {
    val viewModel = hiltViewModel<ReservationsViewModel>()

    OutlinedCard(
        onClick = {
            // when you click on a reservation, you'll be taken to the reserved car's details.
            navController.navigate("carItem/${reservedTimeslot.carId}")
        },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .padding(start = 16.dp)
                    .fillMaxWidth(),
            ) {
                Text(
                    "Reservation",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    "From: ${reservedTimeslot.availableFrom}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    "Until: ${reservedTimeslot.availableUntil}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row {
                    Button(
                        onClick = {
                            viewModel.cancelReservation(reservedTimeslot)
                        },
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f)
                            .padding(end = 4.dp),
                        colors = ButtonDefaults.buttonColors().copy(
                            containerColor = Color.Red
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Clear reservation icon",
                        )
                        Text("Cancel")
                    }
                    Button(
                        onClick = {
                            navController.navigate("directions/${reservedTimeslot.carId}")
                        },
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f)
                            .padding(end = 4.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "route icon",
                        )
                        Text("Route to car")
                    }
                }
            }
        }
    }
}
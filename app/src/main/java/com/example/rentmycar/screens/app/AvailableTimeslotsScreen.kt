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
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.rentmycar.components.TimeSlotCard
import com.example.rentmycar.viewmodel.TimeslotViewModel
import com.example.rentmycar.viewmodel.TimeslotViewState

@Composable
fun AvailableTimeslotsScreen(navController: NavHostController, carId: Int) {
    val viewModel = hiltViewModel<TimeslotViewModel>()

    AuthenticatedScreen(navController, viewModel.logoutEvent) {
        val viewState by viewModel.viewState.collectAsState()

        LaunchedEffect(Unit) {
            viewModel.getAvailableCarTimeSlots(carId)
        }

        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.Top,
            ) {
                Text(
                    "Available Timeslots",
                    style = MaterialTheme.typography.headlineMedium,
                )
                TextButton(
                    onClick = {
                        navController.navigate("carItem/${carId}")
                    },
                    contentPadding = PaddingValues(all = 0.dp),
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = "Back icon",
                    )
                    Text("Back to car")
                }

                when (val state = viewState) {
                    TimeslotViewState.Loading -> {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CircularProgressIndicator()
                        }
                    }

                    is TimeslotViewState.Error -> {
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

                    is TimeslotViewState.Success -> {
                        LazyColumn(modifier = Modifier.fillMaxHeight()) {
                            items(state.availableTimeslots.size) { index ->
                                TimeSlotCard(state.availableTimeslots[index], navController)
                            }
                        }
                    }
                }
            }
        }
    }
}

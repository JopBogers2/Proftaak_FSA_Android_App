package com.example.rentmycar.screens.app.car.owner

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
import androidx.compose.material3.Button
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.rentmycar.R
import com.example.rentmycar.components.ManageTimeslotCard
import com.example.rentmycar.screens.app.AuthenticatedScreen
import com.example.rentmycar.viewmodel.TimeslotsViewModel
import com.example.rentmycar.viewmodel.TimeslotsViewState


@Composable
fun ManageTimeslotsScreen(navController: NavHostController, carId: Int) {
    val viewModel = hiltViewModel<TimeslotsViewModel>()

    AuthenticatedScreen(navController, viewModel.logoutEvent) {
        val viewState by viewModel.viewState.collectAsState()

        LaunchedEffect(Unit) {
            viewModel.getCarTimeSlots(carId)
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
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(
                        "Timeslots",
                        style = MaterialTheme.typography.headlineMedium,
                    )
                    Button(onClick = {
                        navController.navigate("addTimeslot/${carId}")
                    }) {
                        Icon(
                            painter = painterResource(R.drawable.add),
                            contentDescription = "Add icon",
                            modifier = Modifier.padding(end = 4.dp),
                        )
                        Text("Add timeslot")
                    }
                }

                TextButton(
                    onClick = {
                        navController.navigate("myCars")
                    },
                    contentPadding = PaddingValues(all = 0.dp),
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = "Back icon",
                    )
                    Text("Back to my cars")
                }

                when (val state = viewState) {
                    TimeslotsViewState.Loading -> {
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

                    is TimeslotsViewState.Error -> {
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

                    is TimeslotsViewState.Success -> {
                        LazyColumn(modifier = Modifier.fillMaxHeight()) {
                            items(state.availableTimeslots.size) { index ->
                                val timeslot = state.availableTimeslots[index]
                                ManageTimeslotCard(
                                    timeslot,
                                    TimeslotsViewModel.isTimeslotInPast(timeslot)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

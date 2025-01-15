package com.example.rentmycar.screens.app

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.rentmycar.components.ReservationCard
import com.example.rentmycar.viewmodel.ReservationViewModel
import com.example.rentmycar.viewmodel.ReservationViewState

@Composable
fun ReservationScreen(navController: NavHostController) {
    val viewModel = hiltViewModel<ReservationViewModel>()

    AuthenticatedScreen(navController, viewModel.logoutEvent) {
        val viewState by viewModel.viewState.collectAsState()

        LaunchedEffect(Unit) {
            viewModel.getUserReservations()
        }

        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            when (val state = viewState) {
                ReservationViewState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                is ReservationViewState.Error -> {
                    Text(
                        text = state.message,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                is ReservationViewState.Success -> {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxHeight(),
                        verticalArrangement = Arrangement.Top,
                    ) {
                        Text(
                            "Reservations",
                            style = MaterialTheme.typography.headlineMedium,
                        )
                        LazyColumn(modifier = Modifier.fillMaxHeight()) {
                            items(state.reservations.size) { index ->
                                ReservationCard(state.reservations[index], navController)
                            }
                        }
                    }
                }
            }
        }
    }
}

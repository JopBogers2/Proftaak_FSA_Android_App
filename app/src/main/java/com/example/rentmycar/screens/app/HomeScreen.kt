package com.example.rentmycar.screens.app

import android.content.Context
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.rentmycar.components.car.CarCard
import com.example.rentmycar.components.car.FilterComponent
import com.example.rentmycar.viewmodel.CarFiltersViewModel
import com.example.rentmycar.viewmodel.CarsViewModel
import com.example.rentmycar.viewmodel.CarsViewState

@Composable
fun HomeScreen(navController: NavHostController, context: Context) {
    val viewModel = hiltViewModel<CarsViewModel>()

    AuthenticatedScreen(navController, viewModel.logoutEvent) {
        val viewState by viewModel.viewState.collectAsState()

        val carFiltersViewModel = remember { CarFiltersViewModel() }
        val filters by carFiltersViewModel.filters.collectAsState()

        // Refresh the cars each time when the filters are modified
        LaunchedEffect(filters) {
            viewModel.getCars(filters)
        }

        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            when (val state = viewState) {
                CarsViewState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is CarsViewState.Error -> {
                    Text(
                        text = state.message,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is CarsViewState.Success -> {
                    Column (
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxHeight(),
                        verticalArrangement = Arrangement.Top,
                    ) {
                        // Filters section
                        FilterComponent(carFiltersViewModel, filters, context)

                        // Cars section
                        LazyColumn(modifier = Modifier.fillMaxHeight()) {
                            items(state.cars.size) { index ->
                                CarCard(state.cars[index], navController)
                            }
                        }
                    }
                }
            }
        }
    }
}

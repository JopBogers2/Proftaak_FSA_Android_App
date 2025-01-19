package com.example.rentmycar.screens.app.car.owner

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.rentmycar.R
import com.example.rentmycar.screens.app.AuthenticatedScreen
import com.example.rentmycar.viewmodel.TimeslotsViewModel
import com.example.rentmycar.viewmodel.TimeslotsViewState
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTimeslotScreen(
    navController: NavHostController,
    carId: Int
) {
    val viewModel = hiltViewModel<TimeslotsViewModel>()

    AuthenticatedScreen(navController, viewModel.logoutEvent) {
        val viewState by viewModel.viewState.collectAsState()

        var fromDateTime: LocalDateTime? = null
        var untilDateTime: LocalDateTime? = null

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp, 36.dp)
        ) {
            Column {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(
                        stringResource(R.string.timeslots),
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
                        Text(stringResource(R.string.add_timeslot))
                    }
                }

                TextButton(
                    onClick = {
                        navController.navigate("timeslotManagement/${carId}")
                    },
                    contentPadding = PaddingValues(all = 0.dp),
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = "Back icon",
                    )
                    Text(stringResource(R.string.back_to_car_timeslots))
                }

                Row {
                    var showDatePicker by remember { mutableStateOf(false) }
                    val datePickerState = rememberDatePickerState()
                    val selectedDate = datePickerState.selectedDateMillis?.let {
                        convertMillisToDate(it)
                    }.also { fromDateTime = it }

                    Box(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = selectedDate.toString(),
                            onValueChange = { fromDateTime = selectedDate },
                            label = { Text(stringResource(R.string.available_from)) },
                            readOnly = true,
                            trailingIcon = {
                                IconButton(onClick = { showDatePicker = !showDatePicker }) {
                                    Icon(
                                        imageVector = Icons.Default.DateRange,
                                        contentDescription = "Select date"
                                    )
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(64.dp)
                        )

                        if (showDatePicker) {
                            Popup(
                                onDismissRequest = { showDatePicker = false },
                                alignment = Alignment.TopStart
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .offset(y = 64.dp)
                                        .shadow(elevation = 4.dp)
                                        .background(MaterialTheme.colorScheme.surface)
                                        .padding(16.dp)
                                ) {
                                    DatePicker(
                                        state = datePickerState,
                                        showModeToggle = false
                                    )
                                }
                            }
                        }
                    }
                }

                Row {
                    var showDatePicker by remember { mutableStateOf(false) }
                    val datePickerState = rememberDatePickerState()
                    val selectedDate = datePickerState.selectedDateMillis?.let {
                        convertMillisToDate(it)
                    }.also { untilDateTime = it }

                    Box(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = selectedDate.toString(),
                            onValueChange = { },
                            label = { Text(stringResource(R.string.available_until)) },
                            readOnly = true,
                            trailingIcon = {
                                IconButton(onClick = { showDatePicker = !showDatePicker }) {
                                    Icon(
                                        imageVector = Icons.Default.DateRange,
                                        contentDescription = "Select date"
                                    )
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(64.dp)
                        )

                        if (showDatePicker) {
                            Popup(
                                onDismissRequest = { showDatePicker = false },
                                alignment = Alignment.TopStart
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .offset(y = 64.dp)
                                        .shadow(elevation = 4.dp)
                                        .background(MaterialTheme.colorScheme.surface)
                                        .padding(16.dp)
                                ) {
                                    DatePicker(
                                        state = datePickerState,
                                        showModeToggle = false
                                    )
                                }
                            }
                        }
                    }
                }

                Button(
                    onClick = { viewModel.addTimeslot(carId, fromDateTime, untilDateTime) },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(stringResource(R.string.add_timeslot))
                }

                when (val state = viewState) {
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

                    TimeslotsViewState.Loading -> {}
                    is TimeslotsViewState.Success -> {}
                }
            }
        }
    }
}

fun convertMillisToDate(milliseconds: Long): LocalDateTime =
    Instant.fromEpochMilliseconds(milliseconds)
        .toLocalDateTime(TimeZone.currentSystemDefault())

package com.example.rentmycar.components.car

import android.content.Context
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.InputChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.rentmycar.R
import com.example.rentmycar.components.DropdownInput
import com.example.rentmycar.utils.Category.Companion.categories
import com.example.rentmycar.utils.helpers.LocationHelper
import com.example.rentmycar.viewmodel.car.CarFiltersViewModel

@Composable
fun FilterComponent(
    carFiltersViewModel: CarFiltersViewModel,
    filters: Map<String, String>,
    context: Context
) {
    var isDialogOpened by rememberSaveable { mutableStateOf(false) }

    //header
    Text(
        stringResource(R.string.available_cars),
        style = MaterialTheme.typography.headlineMedium,
    )
    // Filters header
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth(),
    ) {
        // sub heading
        Text(
            stringResource(R.string.filters),
            style = MaterialTheme.typography.headlineSmall,
        )
        // Button to expand the filter inputs section
        Button(onClick = { isDialogOpened = true }) {
            Icon(
                painter = painterResource(R.drawable.add),
                contentDescription = stringResource(R.string.add_icon),
                modifier = Modifier.padding(end = 4.dp),
            )
            Text(stringResource(R.string.add_filters))
        }
    }

    // Filter chips
    if (filters.isNotEmpty()) {
        Row(modifier = Modifier
            .height(40.dp)
            .horizontalScroll(rememberScrollState(0))) {
            for ((key, value) in filters.entries) {
                FilterChip(
                    onClick = {
                        // Remove the filter with such key from the array when the chip is clicked
                        carFiltersViewModel.addOrRemoveFilter(key, null)
                    },
                    label = { Text("$key: $value") },
                    selected = filters.containsKey(key),
                    modifier = Modifier.padding(4.dp),
                    trailingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.close),
                            contentDescription = stringResource(R.string.close_icon),
                            Modifier.size(InputChipDefaults.AvatarSize)
                        )
                    }
                )
            }
        }
    }

    // Filters dialog
    if (isDialogOpened) {
        Column(
            modifier = Modifier.fillMaxWidth(),
        ) {
            // Inputs
            FilterInputs(carFiltersViewModel, filters, context)

            // Hide dialog button
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                TextButton(
                    onClick = { isDialogOpened = false }
                ) { Text(stringResource(R.string.hide_filters)) }
            }
        }
    }
}

/**
 * Inputs for providing the filter values
 */
@Composable
fun FilterInputs(
    carFiltersViewModel: CarFiltersViewModel,
    filters: Map<String, String>,
    context: Context
) {
    val locationHelper = LocationHelper(context)

    var category by rememberSaveable { mutableStateOf(filters["category"] ?: "") }
    var minPrice by rememberSaveable { mutableStateOf(filters["minPrice"] ?: "") }
    var maxPrice by rememberSaveable { mutableStateOf(filters["maxPrice"] ?: "") }
    var radius by rememberSaveable { mutableStateOf(filters["radius"] ?: "") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp, bottom = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Category input
        FilterRow(stringResource(R.string.FilterCategory)) {
            DropdownInput(
                initValue = category,
                options = categories,
                onClick = { category = it },
                width = 243.dp,
            )
        }

        // Price (min & max) input
        FilterRow(stringResource(R.string.FilterPrice)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                OutlinedTextField(
                    value = minPrice,
                    onValueChange = { minPrice = it },
                    modifier = Modifier.width(100.dp),
                    singleLine = true,
                )
                Text("$ to ")
                OutlinedTextField(
                    value = maxPrice,
                    onValueChange = { maxPrice = it },
                    modifier = Modifier.width(100.dp),
                    singleLine = true,
                )
                Text("$")
            }
        }

        // Radius input
        FilterRow(stringResource(R.string.radius)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                OutlinedTextField(
                    value = radius,
                    onValueChange = { radius = it },
                    modifier = Modifier.width(230.dp),
                    singleLine = true,
                )
                Text("m")
            }
        }

        // Apply filters button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
        ) {
            Button(
                onClick = {
                    carFiltersViewModel.addOrRemoveFilter("category", category)
                    carFiltersViewModel.addOrRemoveFilter("minPrice", minPrice)
                    carFiltersViewModel.addOrRemoveFilter("maxPrice", maxPrice)
                    carFiltersViewModel.addOrRemoveFilter("radius", radius)

                    // If radius is provided,
                    // then user's location (latitude & longitude) must also be provided.
                    if (radius.isNotEmpty()) {
                        locationHelper.getUserLocation { location ->
                            carFiltersViewModel.addOrRemoveFilter(
                                "latitude",
                                location?.latitude?.toString()
                            )
                            carFiltersViewModel.addOrRemoveFilter(
                                "longitude",
                                location?.longitude?.toString()
                            )
                        }
                    }
                }
            ) { Text(stringResource(R.string.apply_filters)) }
        }
    }
}

/**
 * Row which contains the filter input and its label
 */
@Composable
fun FilterRow(
    label: String,
    input: @Composable () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(label, style = MaterialTheme.typography.titleMedium)
        input()
    }
}
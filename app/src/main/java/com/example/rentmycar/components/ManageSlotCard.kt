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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.rentmycar.R
import com.example.rentmycar.api.responses.TimeslotResponse
import com.example.rentmycar.viewmodel.TimeslotsViewModel

/**
 * Card which contains general info about the car without details or location.
 */
@Composable
fun ManageTimeslotCard(timeslot: TimeslotResponse, pastTimeslot: Boolean = false) {
    val viewModel = hiltViewModel<TimeslotsViewModel>()

    OutlinedCard(
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
                    stringResource(R.string.timeslot),
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    "From: ${timeslot.availableFrom}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    "Until: ${timeslot.availableUntil}",
                    style = MaterialTheme.typography.bodyMedium
                )

                if (pastTimeslot) {
                    Text(stringResource(R.string.timeslot_has_expired))
                }

                Spacer(modifier = Modifier.height(16.dp))
                Row {
                    Button(
                        onClick = {
                            viewModel.removeTimeslot(timeslot)
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
                        Text(stringResource(R.string.remove))
                    }
                }
            }
        }
    }
}
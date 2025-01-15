package com.example.rentmycar.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rentmycar.api.ApiCallHandler
import com.example.rentmycar.api.ApiService
import com.example.rentmycar.api.responses.TimeslotResponse
import com.example.rentmycar.exceptions.ApiException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface TimeslotViewState {
    data class Success(val availableTimeslots: List<TimeslotResponse>) : TimeslotViewState
    data object Loading : TimeslotViewState
    data class Error(val message: String) : TimeslotViewState
}

@HiltViewModel
class TimeslotViewModel @Inject constructor(
    private val apiCallHandler: ApiCallHandler,
    private val apiService: ApiService
) : ViewModel() {
    val logoutEvent = apiCallHandler.logoutEvent

    private val _viewState = MutableStateFlow<TimeslotViewState>(TimeslotViewState.Loading)
    val viewState = _viewState.asStateFlow()

    fun getAvailableCarTimeSlots(carId: Int) {
        viewModelScope.launch {
            try {
                val availableTimeslots = mutableListOf<TimeslotResponse>()

                apiCallHandler.makeApiCall {
                    apiService.getTimeslotsByCarId(carId)
                }.forEach { timeslot ->
                    try {
                        apiCallHandler.makeApiCall {
                            apiService.getTimeslotReservations(timeslot.id)
                        }
                    } catch (e: ApiException) {
                        if (e.errorCode == 404) {
                            availableTimeslots.add(timeslot)
                        }
                    }
                }

                if (availableTimeslots.isEmpty()) {
                    _viewState.update { TimeslotViewState.Error("No timeslots available for car") }
                } else {
                    _viewState.update { TimeslotViewState.Success(availableTimeslots) }
                }
            } catch (e: Exception) {
                _viewState.update { TimeslotViewState.Error(e.message ?: "Unknown error") }
            }
        }
    }
}
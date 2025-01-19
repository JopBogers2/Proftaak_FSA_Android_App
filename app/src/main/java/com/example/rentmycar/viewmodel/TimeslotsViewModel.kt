package com.example.rentmycar.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rentmycar.api.ApiCallHandler
import com.example.rentmycar.api.ApiService
import com.example.rentmycar.api.requests.AddTimeslotRequest
import com.example.rentmycar.api.requests.CreateReservationRequest
import com.example.rentmycar.api.responses.TimeslotResponse
import com.example.rentmycar.exceptions.ApiException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import javax.inject.Inject

sealed interface TimeslotsViewState {
    data class Success(val availableTimeslots: List<TimeslotResponse>) : TimeslotsViewState
    data object Loading : TimeslotsViewState
    data class Error(val message: String) : TimeslotsViewState
}

@HiltViewModel
class TimeslotsViewModel @Inject constructor(
    private val apiCallHandler: ApiCallHandler,
    private val apiService: ApiService
) : ViewModel() {
    val logoutEvent = apiCallHandler.logoutEvent

    private val _viewState = MutableStateFlow<TimeslotsViewState>(TimeslotsViewState.Loading)
    val viewState = _viewState.asStateFlow()

    private val availableTimeslots = mutableListOf<TimeslotResponse>()

    fun getCarTimeSlots(carId: Int) {
        viewModelScope.launch {
            try {
                availableTimeslots.clear()
                apiCallHandler.makeApiCall {
                    apiService.getTimeslotsByCarId(carId)
                }.forEach { timeslot -> availableTimeslots.add(timeslot) }

                if (availableTimeslots.isEmpty()) {
                    _viewState.update { TimeslotsViewState.Error("No timeslots available for car") }
                } else {
                    _viewState.update { TimeslotsViewState.Success(availableTimeslots) }
                }
            } catch (e: Exception) {
                _viewState.update { TimeslotsViewState.Error(e.message ?: "Unknown error") }
            }
        }
    }

    fun getReservableCarTimeSlots(carId: Int) {
        viewModelScope.launch {
            try {
                apiCallHandler.makeApiCall {
                    apiService.getTimeslotsByCarId(carId)
                }.forEach { timeslot ->
                    try {
                        apiCallHandler.makeApiCall {
                            apiService.getTimeslotReservation(timeslot.id)
                        }
                    } catch (e: ApiException) {
                        // todo: this can be done in a better manner, but works for now;
                        if (e.errorCode == 404) {
                            availableTimeslots.add(timeslot)
                        }
                    }
                }
                if (availableTimeslots.isEmpty()) {
                    _viewState.update { TimeslotsViewState.Error("No timeslots available for car") }
                } else {
                    _viewState.update { TimeslotsViewState.Success(availableTimeslots) }
                }
            } catch (e: Exception) {
                _viewState.update { TimeslotsViewState.Error(e.message ?: "Unknown error") }
            }
        }
    }

    fun reserveTimeslot(timeslot: TimeslotResponse) {
        viewModelScope.launch {
            _viewState.update { TimeslotsViewState.Loading }
            try {
                apiCallHandler.makeApiCall {
                    apiService.createReservation(
                        CreateReservationRequest(
                            timeslot.id
                        )
                    )
                }.let {
                    availableTimeslots.remove(timeslot)
                    _viewState.update { TimeslotsViewState.Success(availableTimeslots) }
                }
            } catch (e: Exception) {
                _viewState.update { TimeslotsViewState.Error(e.message ?: "Unknown error") }
            }
        }
    }

    fun addTimeslot(carId: Int, fromDateTime: LocalDateTime?, untilDateTime: LocalDateTime?) {
        if (fromDateTime != null || untilDateTime != null) {
            viewModelScope.launch {
                try {
                    apiCallHandler.makeApiCall {
                        apiService.createTimeslot(
                            AddTimeslotRequest(
                                carId,
                                fromDateTime!!,
                                untilDateTime!!
                            )
                        )
                    }

                    _viewState.update { TimeslotsViewState.Error("Added Timeslot") }
                } catch (e: Exception) {
                    _viewState.update { TimeslotsViewState.Error(e.message ?: "unknown error") }
                }
            }
        } else {
            _viewState.update { TimeslotsViewState.Error("unable to parse date") }
        }
    }

    fun removeTimeslot(timeslot: TimeslotResponse) {
        viewModelScope.launch {
            try {
                _viewState.update { TimeslotsViewState.Loading }
                apiCallHandler.makeApiCall {
                    apiService.deleteTimeslotById(timeslot.id)
                }.let {
                    availableTimeslots.remove(timeslot)
                    if (availableTimeslots.isEmpty()) {
                        _viewState.update { TimeslotsViewState.Error("No timeslots available for car") }
                    } else {
                        _viewState.update { TimeslotsViewState.Success(availableTimeslots) }
                    }
                }
            } catch (e: Exception) {
                _viewState.update { TimeslotsViewState.Error(e.message ?: "Unknown error") }
            }
        }
    }

    companion object {
        fun isTimeslotInPast(timeslot: TimeslotResponse) =
            timeslot.availableFrom < Clock.System.now()
                .toLocalDateTime(TimeZone.currentSystemDefault())
    }
}




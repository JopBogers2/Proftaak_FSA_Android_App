package com.example.rentmycar.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rentmycar.api.ApiCallHandler
import com.example.rentmycar.api.ApiService
import com.example.rentmycar.api.responses.TimeslotResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface ReservationViewState {
    data class Success(val reservations: List<TimeslotResponse>) : ReservationViewState
    data object Loading : ReservationViewState
    data class Error(val message: String) : ReservationViewState
}

@HiltViewModel
class ReservationViewModel @Inject constructor(
    private val apiCallHandler: ApiCallHandler,
    private val apiService: ApiService
) : ViewModel() {
    val logoutEvent = apiCallHandler.logoutEvent

    private val _viewState = MutableStateFlow<ReservationViewState>(ReservationViewState.Loading)
    val viewState = _viewState.asStateFlow()

    fun getUserReservations() {
        viewModelScope.launch {
            try {
                val reservedTimeslots = mutableListOf<TimeslotResponse>()
                // Fetch user reservations, and retrieve associated timeslots.
                apiCallHandler.makeApiCall {
                    apiService.getUserReservations()
                }.forEach { reservation ->
                    reservedTimeslots.add(
                        apiCallHandler.makeApiCall {
                            apiService.getTimeslotById(reservation.timeslotId)
                        }
                    )
                }

                _viewState.update { ReservationViewState.Success(reservedTimeslots) }
            } catch (e: Exception) {
                _viewState.update { ReservationViewState.Error(e.message ?: "Unknown error") }
            }
        }
    }
}
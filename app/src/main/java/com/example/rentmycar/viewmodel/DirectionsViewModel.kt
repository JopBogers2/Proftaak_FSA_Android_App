package com.example.rentmycar.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rentmycar.api.ApiCallHandler
import com.example.rentmycar.api.ApiService
import com.example.rentmycar.api.requests.DirectionsToCarRequest
import com.example.rentmycar.api.responses.DirectionsToCarResponse
import com.example.rentmycar.utils.helpers.LocationHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface DirectionsViewState {
    data class Success(val directions: List<DirectionsToCarResponse>) : DirectionsViewState
    data object Loading : DirectionsViewState
    data class Error(val message: String) : DirectionsViewState
}

@HiltViewModel
class DirectionsViewModel @Inject constructor(
    private val apiCallHandler: ApiCallHandler,
    private val apiService: ApiService,
    @ApplicationContext private val context: Context
) : ViewModel() {
    val logoutEvent = apiCallHandler.logoutEvent

    private val _viewState = MutableStateFlow<DirectionsViewState>(DirectionsViewState.Loading)

    val viewState = _viewState.asStateFlow()
    fun getDirections(carId: Int) {
        val locationHelper = LocationHelper(context)

        locationHelper.getUserLocation { location ->
            viewModelScope.launch {
                try {
                    val directions = mutableListOf<DirectionsToCarResponse>()

                    if (location != null) {
                        directions.add(apiCallHandler.makeApiCall {
                            apiService.getDirectionsToCar(
                                DirectionsToCarRequest(
                                    location.latitude,
                                    location.longitude,
                                    carId
                                )
                            )
                        })

                        if (directions[0].routes.isEmpty())
                            _viewState.update {
                                // only supports walking route, car needs to be on same continent.
                                DirectionsViewState.Error("Unable to find route")
                            }
                        else
                            _viewState.update { DirectionsViewState.Success(directions) }
                    } else {
                        _viewState.update {
                            DirectionsViewState.Error("Unable to determine phone location")
                        }
                    }
                } catch (e: Exception) {
                    _viewState.update {
                        DirectionsViewState.Error(
                            e.message ?: "Unknown error"
                        )
                    }
                }
            }
        }
    }
}
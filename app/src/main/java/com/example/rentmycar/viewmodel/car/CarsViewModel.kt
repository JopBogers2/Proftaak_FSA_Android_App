package com.example.rentmycar.viewmodel.car
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rentmycar.api.ApiCallHandler
import com.example.rentmycar.api.ApiService
import com.example.rentmycar.api.responses.CarResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface CarsViewState {
    data class Success(val cars: List<CarViewModel>) : CarsViewState
    data object Loading : CarsViewState
    data class Error(val message: String) : CarsViewState
}

@HiltViewModel
class CarsViewModel @Inject constructor(
    private val apiCallHandler: ApiCallHandler,
    private val apiService: ApiService
) : ViewModel() {
    val logoutEvent = apiCallHandler.logoutEvent

    private val _viewState = MutableStateFlow<CarsViewState>(CarsViewState.Loading)
    val viewState = _viewState.asStateFlow()

    /**
     * Get all the cars
     */
    fun getCars(
        filters: Map<String, String>,
    ) {
        viewModelScope.launch {
            try {
                val response = apiCallHandler.makeApiCall {
                    apiService.getFilteredCars(filters)
                }
                initCars(response)
            } catch (e: Exception) {
                _viewState.update { CarsViewState.Error(e.message ?: "Unknown error") }
            }
        }
    }

    /**
     * Initialize the fetched cars via converting them to view models
     * with additional info like images and location
     */
    private fun initCars(rawCars: List<CarResponse>) {
        val initializedCars = rawCars.map { rawCar ->
            val carViewModel = CarViewModel(apiCallHandler, apiService)

            viewModelScope.launch {
                carViewModel.initCar(rawCar)
            }

            carViewModel
        }

        _viewState.update { CarsViewState.Success(initializedCars) }
    }
}
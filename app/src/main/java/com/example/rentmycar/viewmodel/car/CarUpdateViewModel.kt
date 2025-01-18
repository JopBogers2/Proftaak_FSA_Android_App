package com.example.rentmycar.viewmodel.car

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rentmycar.api.requests.UpdateCarRequest
import com.example.rentmycar.api.responses.OwnedCarResponse
import com.example.rentmycar.repository.CarRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CarUpdateViewModel @Inject constructor(
    private val carRepository: CarRepository
) : ViewModel() {

    private val _updateState = MutableStateFlow<UpdateState>(UpdateState.Idle)
    val updateState: StateFlow<UpdateState> = _updateState.asStateFlow()

    private val _carDetailsMap = MutableStateFlow<Map<Int, OwnedCarResponse>>(emptyMap())
    val carDetailsMap: StateFlow<Map<Int, OwnedCarResponse>> = _carDetailsMap.asStateFlow()



    fun getCarDetails(carId: Int) {
        viewModelScope.launch {
            try {
                _updateState.value = UpdateState.Loading
                val cars = carRepository.getOwnerCars()
                val car = cars.find { it.id == carId }
                if (car != null) {
                    _carDetailsMap.value = _carDetailsMap.value + (carId to car)
                    _updateState.value = UpdateState.Idle
                } else {
                    Log.e("CarUpdateViewModel", "Car not found with ID: $carId")
                    _updateState.value = UpdateState.Error(
                        "Failed to fetch car details: Car not found",
                        emptyMap()
                    )
                }
            } catch (e: Exception) {
                Log.e("CarUpdateViewModel", "Exception while fetching car details", e)
                _updateState.value = UpdateState.Error("Unexpected error: ${e.message}", emptyMap())
            }
        }
    }

    fun updateCar(car: OwnedCarResponse) {
        viewModelScope.launch {
            _updateState.value = UpdateState.Loading
            try {
                val request = UpdateCarRequest(
                    carId = car.id,
                year = car.year,
                color = car.color,
                transmission = car.transmission,
                fuel = car.fuel,
                price = car.price,
                )

                Log.d("CarUpdateViewModel", "Updating car: $request")

                val result = carRepository.updateCar(request)
                if (result.isSuccess) {
                    _updateState.value = UpdateState.Success("Car updated successfully")
                    _carDetailsMap.value = _carDetailsMap.value + (car.id to car)
                    refreshCarList()
                } else {
                    _updateState.value = UpdateState.Error("Failed to update car: ${result.exceptionOrNull()?.message}", emptyMap())
                }
            } catch (e: Exception) {
                Log.e("CarUpdateViewModel", "Exception while updating car", e)
                _updateState.value = UpdateState.Error("Unexpected error: ${e.message}", emptyMap())
            }
        }
    }

    private fun refreshCarList() {
        viewModelScope.launch {
            try {
                val updatedCars = carRepository.getOwnerCars()
                _carDetailsMap.value = updatedCars.associateBy { it.id }
            } catch (e: Exception) {
                Log.e("CarUpdateViewModel", "Failed to refresh car list", e)
            }
        }
    }

    private fun parseFieldErrors(errorMessage: String): Map<String, String> {
        val fieldErrors = mutableMapOf<String, String>()
        val errorParts = errorMessage.split(";")
        for (part in errorParts) {
            val keyValue = part.split(":")
            if (keyValue.size == 2) {
                fieldErrors[keyValue[0].trim()] = keyValue[1].trim()
            }
        }
        return fieldErrors
    }
}

sealed class UpdateState {
    object Idle : UpdateState()
    object Loading : UpdateState()
    data class Success(val message: String) : UpdateState()
    data class Error(val message: String, val fieldErrors: Map<String, String> = emptyMap()) : UpdateState()
}
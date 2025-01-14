package com.example.rentmycar.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rentmycar.api.requests.CarDTO
import com.example.rentmycar.repository.CarRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserCarsViewModel @Inject constructor(
    private val carRepository: CarRepository
) : ViewModel() {

    private val _viewState = MutableStateFlow<UserCarsViewState>(UserCarsViewState.Loading)
    val viewState: StateFlow<UserCarsViewState> = _viewState

    fun getUserCars() {
        viewModelScope.launch {
            _viewState.value = UserCarsViewState.Loading
            try {
                val cars = carRepository.getOwnerCars()
                if (cars.isEmpty()) {
                    _viewState.value = UserCarsViewState.NoCars
                } else {
                    _viewState.value = UserCarsViewState.Success(cars)
                }
            } catch (e: Exception) {
                _viewState.value = UserCarsViewState.Error(e.message ?: "Unknown error occurred")
            }
        }
    }
}

sealed class UserCarsViewState {
    object Loading : UserCarsViewState()
    data class Success(val cars: List<CarDTO>) : UserCarsViewState()
    object NoCars : UserCarsViewState()
    data class Error(val message: String) : UserCarsViewState()
}
package com.example.rentmycar.viewmodel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rentmycar.api.ApiCallHandler
import com.example.rentmycar.api.ApiService
import com.example.rentmycar.api.requests.CarResponse
import com.example.rentmycar.api.requests.UserResponse
import com.example.rentmycar.exceptions.ApiException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface CarViewState {
    data class Success(val cars: List<CarResponse>) : CarViewState
    data object Loading : CarViewState
    data class Error(val message: String) : CarViewState
}

@HiltViewModel
class CarViewModel @Inject constructor(
    private val apiCallHandler: ApiCallHandler,
    private val apiService: ApiService
) : ViewModel() {
    val logoutEvent = apiCallHandler.logoutEvent

    private val _viewState = MutableStateFlow<CarViewState>(CarViewState.Loading)
    val viewState = _viewState.asStateFlow()

    fun getCars() {
        viewModelScope.launch {
            try {
                val response = apiCallHandler.makeApiCall {
                    apiService.getFilteredCars()
                }
                _viewState.update { CarViewState.Success(response) }
            } catch (e: Exception) {
                _viewState.update { CarViewState.Error(e.message ?: "Unknown error") }
            }
        }
    }
}
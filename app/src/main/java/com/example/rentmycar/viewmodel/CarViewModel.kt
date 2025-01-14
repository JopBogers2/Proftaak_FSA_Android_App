package com.example.rentmycar.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rentmycar.api.ApiCallHandler
import com.example.rentmycar.api.ApiService
import com.example.rentmycar.api.requests.CarLocationResponse
import com.example.rentmycar.api.requests.CarResponse
import com.example.rentmycar.exceptions.ApiException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CarViewModel @Inject constructor(
    private val apiCallHandler: ApiCallHandler,
    private val apiService: ApiService
) : ViewModel() {
    val logoutEvent = apiCallHandler.logoutEvent

    private val _car = MutableLiveData<CarResponse>()
    val car: LiveData<CarResponse> get() = _car

    private val _images = MutableLiveData<List<String>>()
    val images: LiveData<List<String>> get() = _images

    private val _location = MutableLiveData<CarLocationResponse>()
    val location: LiveData<CarLocationResponse> get() = _location

    /**
     * Fetched the images attached to the car
     */
    private fun loadImages(id: Int) {
        viewModelScope.launch {
            try {
                val response = apiCallHandler.makeApiCall {
                    apiService.getImagesByCar(id)
                }
                _images.postValue(response)
            } catch (e: ApiException) {
                // TODO
            }
        }
    }

    /**
     * Fetch the location of the car
     */
    private fun loadLocation(id: Int) {
        viewModelScope.launch {
            try {
                val response = apiCallHandler.makeApiCall {
                    apiService.getLocationByCar(id)
                }
                _location.postValue(response)
            } catch (e: ApiException) {
                // TODO
            }
        }
    }

    /**
     * Fetch the car in case it was not provided initially
     */
    private fun loadCar(carId: Int) {
        viewModelScope.launch {
            try {
                val response = apiCallHandler.makeApiCall {
                    apiService.getCar(carId)
                }
                _car.postValue(response)
            } catch (e: ApiException) {
                // TODO
            }
        }
    }

    /**
     * Initialize the car via fetching it
     */
    fun initCar(carId: Int) {
        viewModelScope.launch {
            loadCar(carId)
            loadImages(carId)
            loadLocation(carId)
        }
    }

    /**
     * Initialize the car via fetching additional info only
     */
    fun initCar(car: CarResponse, initLocation: Boolean = false) {
        viewModelScope.launch {
            _car.postValue(car)
            loadImages(car.id)

            if (initLocation) {
                loadLocation(car.id)
            }
        }
    }
}
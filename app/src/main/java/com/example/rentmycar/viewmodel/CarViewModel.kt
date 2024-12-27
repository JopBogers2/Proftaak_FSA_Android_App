package com.example.rentmycar.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rentmycar.PreferencesManager
import com.example.rentmycar.api.ApiClient
import com.example.rentmycar.api.makeApiCall
import com.example.rentmycar.api.requests.CarResponse
import com.example.rentmycar.exceptions.ApiException
import kotlinx.coroutines.launch

class CarViewModel(context: Context) : ViewModel() {
    private val apiService = ApiClient.createApiService(PreferencesManager(context).jwtToken)

    fun getCars(onSuccess: (List<CarResponse>) -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val response = makeApiCall {
                    apiService.getFilteredCars()
                }
                onSuccess(response)
            } catch (e: Exception) {
                onError(e.message ?: "Unknown error")
            }
        }
    }
}
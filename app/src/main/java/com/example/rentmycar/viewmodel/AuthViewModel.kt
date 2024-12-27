package com.example.rentmycar.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rentmycar.PreferencesManager
import com.example.rentmycar.api.ApiClient
import com.example.rentmycar.api.makeApiCall
import com.example.rentmycar.api.requests.LoginRequest
import com.example.rentmycar.api.requests.RegisterRequest
import kotlinx.coroutines.launch

class AuthViewModel(private val preferencesManager: PreferencesManager) : ViewModel() {
    private val apiService = ApiClient.createApiService(null)

    fun login(email: String, password: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val response = makeApiCall {
                    apiService.login(LoginRequest(email, password))
                }
                preferencesManager.jwtToken = response.token
                onSuccess()
            } catch (e: Exception) {
                onError(e.message ?: "Unknown error")
            }
        }
    }

    fun register(firstName: String, lastName: String, username: String, email: String, password: String,  onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val response = makeApiCall {
                    apiService.register(RegisterRequest(firstName, lastName, username, email, password))
                }
                preferencesManager.jwtToken = response.token
                onSuccess()
            } catch (e: Exception) {
                onError(e.message ?: "Unknown error")
            }
        }
    }
}
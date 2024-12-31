package com.example.rentmycar.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rentmycar.PreferencesManager
import com.example.rentmycar.api.ApiCallHandler
import com.example.rentmycar.api.ApiService
import com.example.rentmycar.api.requests.LoginRequest
import com.example.rentmycar.api.requests.RegisterRequest

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface AuthViewState {
    data object Initial : AuthViewState
    data object Success : AuthViewState
    data object Loading : AuthViewState
    data class Error(val message: String) : AuthViewState
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val apiCallHandler: ApiCallHandler,
    private val apiService: ApiService,
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    private val _viewState = MutableStateFlow<AuthViewState>(AuthViewState.Initial)
    val viewState = _viewState.asStateFlow()

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _viewState.update { AuthViewState.Loading }
            try {
                val response = apiCallHandler.makeApiCall {
                    apiService.login(LoginRequest(email, password))
                }
                preferencesManager.jwtToken = response.token
                _viewState.update { AuthViewState.Success }
            } catch (e: Exception) {
                _viewState.update { AuthViewState.Error(e.message ?: "Unknown error") }
            }
        }
    }

    fun register(
        firstName: String,
        lastName: String,
        username: String,
        email: String,
        password: String
    ) {
        viewModelScope.launch {
            try {
                _viewState.update { AuthViewState.Loading }

                val response = apiCallHandler.makeApiCall {
                    apiService.register(
                        RegisterRequest(
                            firstName,
                            lastName,
                            username,
                            email,
                            password
                        )
                    )
                }
                preferencesManager.jwtToken = response.token

                _viewState.update { AuthViewState.Success }
            } catch (e: Exception) {
                _viewState.update { AuthViewState.Error(e.message ?: "Unknown error") }
            }
        }
    }
}
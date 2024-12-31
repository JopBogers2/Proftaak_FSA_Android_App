package com.example.rentmycar.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rentmycar.api.ApiCallHandler
import com.example.rentmycar.api.ApiService
import com.example.rentmycar.api.requests.UserResponse
import com.example.rentmycar.exceptions.ApiException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface ProfileViewState {
    data object Loading : ProfileViewState
    data class Error(val message: String) : ProfileViewState
    data class Success(val user: UserResponse) : ProfileViewState
}

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val apiCallHandler: ApiCallHandler,
    private val apiService: ApiService
) : ViewModel() {
    val logoutEvent = apiCallHandler.logoutEvent

    private val _viewState = MutableStateFlow<ProfileViewState>(ProfileViewState.Loading)
    val viewState = _viewState.asStateFlow()

    fun loadUserData() {
        viewModelScope.launch {
            try {
                val response = apiCallHandler.makeApiCall { apiService.getUserScore() }
                _viewState.update { ProfileViewState.Success(response) }
            } catch (e: ApiException) {
                _viewState.update { ProfileViewState.Error(e.message)}
            }
        }
    }
}
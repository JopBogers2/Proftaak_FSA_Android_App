package com.example.rentmycar.api

import com.example.rentmycar.PreferencesManager
import com.example.rentmycar.exceptions.ApiException
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import retrofit2.Response
import javax.inject.Inject

class ApiCallHandler @Inject constructor(
    private val preferencesManager: PreferencesManager
) {

    private val _logoutEvent = MutableSharedFlow<Unit>()
    val logoutEvent: SharedFlow<Unit> = _logoutEvent

    suspend fun <T> makeApiCall(apiCall: suspend () -> Response<T>): T {
        val response = apiCall()
        if (response.isSuccessful) {
            return response.body()!!
        } else {
            val errorBody = response.errorBody()?.string() ?: "Unknown error"
            if (response.code() == 401) {
                handleLogout()
            }
            throw ApiException(response.code(), errorBody)
        }
    }

    private suspend fun handleLogout() {
        preferencesManager.clearToken()
        _logoutEvent.emit(Unit)
    }
}

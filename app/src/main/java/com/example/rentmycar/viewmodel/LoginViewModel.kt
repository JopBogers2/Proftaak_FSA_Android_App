package com.example.rentmycar.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rentmycar.api.LoginRequest
import com.example.rentmycar.api.RetrofitClient
import com.example.rentmycar.api.handleApiRequest
import com.example.rentmycar.datastore.JwtDataStore
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    fun login(
        context: Context,
        email: String,
        password: String,
        onSuccess: (String) -> Unit,
        onError: (List<String>) -> Unit
    ) {
        viewModelScope.launch {
            handleApiRequest(
                apiCall = RetrofitClient.apiService.login(LoginRequest(email, password)),
                onError = onError
            ) { result ->
                viewModelScope.launch {
                    JwtDataStore.saveToken(context, result.data.token)
                    onSuccess(result.data.token)
                }
            }
        }
    }
}

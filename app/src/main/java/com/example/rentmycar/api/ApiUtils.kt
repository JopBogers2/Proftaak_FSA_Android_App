package com.example.rentmycar.api

import com.example.rentmycar.exceptions.ApiException
import retrofit2.Response

suspend fun <T> makeApiCall(apiCall: suspend () -> Response<T>): T {
    val response = apiCall()
    if (response.isSuccessful) {
        return response.body()!!
    } else {
        val errorBody = response.errorBody()?.string() ?: "Unknown error"
        throw ApiException(response.code(), errorBody)
    }
}
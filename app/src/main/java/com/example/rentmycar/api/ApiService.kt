package com.example.rentmycar.api

import com.google.gson.Gson
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

data class LoginRequest(val email: String, val password: String)
data class LoginResponse(val token: String)
data class ErrorResponse(val errors: List<String>)

interface ApiService {
    @POST("user/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>
}

fun <T> handleApiRequest(
    apiCall: Response<T>,
    onError: (List<String>) -> Unit,
    onSuccess: (ApiResult.Success<T>) -> Unit
) {
    try {
        when (val result = handleApiResponse(apiCall)) {
            is ApiResult.Success -> onSuccess(result)
            is ApiResult.Error -> onError(result.errorResponse.errors)
        }
    } catch (e: Exception) {
        onError(listOf("An unknown error occurred"))
    }
}

sealed class ApiResult<out T> {
    data class Success<T>(val data: T) : ApiResult<T>()
    data class Error(val errorResponse: ErrorResponse) : ApiResult<Nothing>()
}

fun <T> handleApiResponse(response: Response<T>): ApiResult<T> {
    if (response.isSuccessful) {
        val responseBody = response.body()
            ?: throw IllegalStateException("Response body is null despite successful response")

        return ApiResult.Success(responseBody)
    }

    val errorBody = response.errorBody()
        ?: throw IllegalStateException("Error response body is null")

    try {
        val errorResponse = Gson().fromJson(errorBody.string(), ErrorResponse::class.java)
        return ApiResult.Error(errorResponse)
    } catch (e: Exception) {
        throw IllegalStateException("Failed to parse error response: ${e.message}", e)
    }
}
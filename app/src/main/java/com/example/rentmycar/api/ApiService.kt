package com.example.rentmycar.api

import com.example.rentmycar.api.requests.CarResponse
import com.example.rentmycar.api.requests.LoginRequest
import com.example.rentmycar.api.requests.MessageResponse
import com.example.rentmycar.api.requests.RegisterRequest
import com.example.rentmycar.api.requests.UserResponse
import com.example.rentmycar.api.requests.UserUpdateRequest
import com.example.rentmycar.api.responses.AuthResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT

interface ApiService {
    @POST("user/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @POST("user/register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>

    @GET("user")
    suspend fun getUser(): Response<UserResponse>

    @DELETE("user/delete")
    suspend fun deleteUser(): Response<MessageResponse>

    @PUT("user/update")
    suspend fun updateUser(@Body request: UserUpdateRequest): Response<MessageResponse>

    @GET("car/all/filtered")
    suspend fun getFilteredCars(): Response<List<CarResponse>>
}

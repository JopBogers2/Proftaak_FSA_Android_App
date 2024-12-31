package com.example.rentmycar.api

import com.example.rentmycar.api.requests.CarResponse
import com.example.rentmycar.api.requests.LoginRequest
import com.example.rentmycar.api.requests.RegisterRequest
import com.example.rentmycar.api.requests.UserResponse
import com.example.rentmycar.api.responses.AuthResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {
    @POST("user/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @POST("user/register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>

    @GET("user/score")
    suspend fun getUserScore(): Response<UserResponse>

    @GET("car/all/filtered")
    suspend fun getFilteredCars(): Response<List<CarResponse>>
}

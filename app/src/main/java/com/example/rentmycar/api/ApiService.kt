package com.example.rentmycar.api

import com.example.rentmycar.api.requests.CarLocationResponse
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
import retrofit2.http.Path
import retrofit2.http.QueryMap

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
    suspend fun getFilteredCars(
        @QueryMap filters: Map<String, String>
    ): Response<List<CarResponse>>

    @GET("car/{id}")
    suspend fun getCar(
        @Path("id") carId: Int
    ): Response<CarResponse>

    @GET("image/car/{id}")
    suspend fun getImagesByCar(
        @Path("id") carId: Int,
    ): Response<List<String>>

    @GET("car/{id}/location")
    suspend fun getLocationByCar(
        @Path("id") carId: Int,
    ): Response<CarLocationResponse>
}

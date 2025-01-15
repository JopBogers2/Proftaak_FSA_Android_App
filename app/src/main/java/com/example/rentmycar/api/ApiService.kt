package com.example.rentmycar.api

import com.example.rentmycar.api.requests.DirectionsToCarRequest
import com.example.rentmycar.api.responses.CarLocationResponse
import com.example.rentmycar.api.responses.CarResponse
import com.example.rentmycar.api.requests.LoginRequest
import com.example.rentmycar.api.responses.MessageResponse
import com.example.rentmycar.api.requests.RegisterRequest
import com.example.rentmycar.api.responses.ReservationResponse
import com.example.rentmycar.api.responses.TimeslotResponse
import com.example.rentmycar.api.responses.UserResponse
import com.example.rentmycar.api.requests.UserUpdateRequest
import com.example.rentmycar.api.responses.AuthResponse
import com.example.rentmycar.api.responses.DirectionsToCarResponse
import com.squareup.moshi.Json
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

    @POST("car/directions")
    suspend fun getDirectionsToCar(@Body request: DirectionsToCarRequest)
            : Response<DirectionsToCarResponse>

    @GET("reservation/user")
    suspend fun getUserReservations(): Response<List<ReservationResponse>>

    @GET("/reservation/timeslot/{id}")
    suspend fun getTimeslotReservations(
        @Path("id") timeSlotId: Int,
    ): Response<List<ReservationResponse>>

    @GET("timeSlot/{id}")
    suspend fun getTimeslotById(
        @Path("id") timeSlotId: Int,
    ): Response<TimeslotResponse>

    @GET("timeSlot/car/{id}")
    suspend fun getTimeslotsByCarId(
        @Path("id") timeSlotId: Int,
    ): Response<List<TimeslotResponse>>
}

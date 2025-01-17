package com.example.rentmycar.api

import com.example.rentmycar.api.requests.BrandDTO
import com.example.rentmycar.api.requests.CarDTO
import com.example.rentmycar.api.requests.CreateReservationRequest
import com.example.rentmycar.api.requests.DirectionsToCarRequest
import com.example.rentmycar.api.requests.LocationRequest
import com.example.rentmycar.api.requests.LoginRequest
import com.example.rentmycar.api.requests.ModelDTO
import com.example.rentmycar.api.requests.RegisterCarRequest
import com.example.rentmycar.api.requests.RegisterRequest
import com.example.rentmycar.api.requests.UserUpdateRequest

import com.example.rentmycar.api.responses.AuthResponse
import com.example.rentmycar.api.responses.CarLocationResponse
import com.example.rentmycar.api.responses.CarResponse
import com.example.rentmycar.api.responses.DirectionsToCarResponse
import com.example.rentmycar.api.responses.LocationResponse
import com.example.rentmycar.api.responses.MessageResponse
import com.example.rentmycar.api.responses.ReservationResponse
import com.example.rentmycar.api.responses.TimeslotResponse
import com.example.rentmycar.api.responses.UserResponse

import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
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

    @GET("brand/all")
    suspend fun getBrands(): Response<List<BrandDTO>>

    @GET("model/brand/{brandId}")
    suspend fun getModelsByBrand(@Path("brandId") brandId: Int): Response<List<ModelDTO>>

    @POST("car/location")
    suspend fun addCarLocation(@Body request: LocationRequest): Response<Unit>

    @PUT("car/location")
    suspend fun updateCarLocation(@Body request: LocationRequest): Response<Unit>

    @GET("car/{carId}/location")
    suspend fun getCarLocation(@Path("carId") carId: Int): Response<LocationResponse>


    @POST("car/register")
    suspend fun registerCar(@Body request: RegisterCarRequest): Response<ResponseBody>

    @GET("car/owner")
    suspend fun getOwnerCars(): Response<List<CarDTO>>

    @GET("car/all/filtered")
    suspend fun getFilteredCars(
        @QueryMap filters: Map<String, String>
    ): Response<List<CarResponse>>

    @GET("car/{id}")
    suspend fun getCar(
        @Path("id") carId: Int
    ): Response<CarResponse>


    @Multipart
    @POST("image/car/{id}")
    suspend fun uploadCarImage(
    @Path("id") carId: Int,
    @Part image: MultipartBody.Part
    ): Response<ResponseBody>

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
    suspend fun getTimeslotReservation(
        @Path("id") timeSlotId: Int,
    ): Response<ReservationResponse>


    @POST("/reservation/create")
    suspend fun createReservation(
        @Body request: CreateReservationRequest
    ): Response<MessageResponse>


    @DELETE("/reservation/{id}")
    suspend fun cancelReservation(
        @Path("id") reservationId: Int,
    ): Response<MessageResponse>


    @GET("timeSlot/{id}")
    suspend fun getTimeslotById(
        @Path("id") timeSlotId: Int,
    ): Response<TimeslotResponse>

    @GET("timeSlot/car/{id}")
    suspend fun getTimeslotsByCarId(
        @Path("id") timeSlotId: Int,
    ): Response<List<TimeslotResponse>>
}

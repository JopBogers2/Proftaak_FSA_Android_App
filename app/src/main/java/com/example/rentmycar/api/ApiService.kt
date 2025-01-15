package com.example.rentmycar.api

import com.example.rentmycar.api.requests.BrandDTO
import com.example.rentmycar.api.requests.CarResponse
import com.example.rentmycar.api.requests.LocationRequest
import com.example.rentmycar.api.requests.LoginRequest
import com.example.rentmycar.api.requests.MessageResponse
import com.example.rentmycar.api.requests.ModelDTO
import com.example.rentmycar.api.requests.RegisterCarRequest
import com.example.rentmycar.api.requests.RegisterRequest
import com.example.rentmycar.api.requests.UserResponse
import com.example.rentmycar.api.requests.UserUpdateRequest
import com.example.rentmycar.api.responses.AuthResponse
import com.example.rentmycar.api.responses.LocationResponse
import com.example.rentmycar.api.requests.CarDTO
import com.example.rentmycar.api.requests.CarLocationResponse


import okhttp3.ResponseBody
import okhttp3.MultipartBody
import retrofit2.http.*
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query
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


    @GET("brand/all")  // Ensure this matches your backend route
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
}
data class MessageResponse(val message: String)
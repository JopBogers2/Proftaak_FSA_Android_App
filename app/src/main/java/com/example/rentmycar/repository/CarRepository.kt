package com.example.rentmycar.repository

import android.util.Log
import com.example.rentmycar.api.ApiService
import com.example.rentmycar.api.responses.BrandResponse
import com.example.rentmycar.api.responses.OwnedCarResponse
import com.example.rentmycar.api.requests.LocationRequest
import com.example.rentmycar.api.responses.ModelResponse
import com.example.rentmycar.api.requests.RegisterCarRequest
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.IOException
import javax.inject.Inject


class CarRepository @Inject constructor(private val apiService: ApiService) {
    suspend fun getOwnerCars(): List<OwnedCarResponse> {
        val response = apiService.getOwnerCars()
        if (response.isSuccessful) {
            return response.body() ?: emptyList()
        } else {
            throw Exception("Failed to fetch owner cars: ${response.errorBody()?.string()}")
        }
    }

    suspend fun getImagesByCar(carId: Int): Result<List<String>> {
        return try {
            val response = apiService.getImagesByCar(carId)
            if (response.isSuccessful) {
                val images = response.body() ?: emptyList()
                Log.d("CarRepository", "Fetched images for car $carId: $images")
                Result.success(images)
            } else {
                val errorBody = response.errorBody()?.string()
                val errorCode = response.code()
                Log.e(
                    "CarRepository",
                    "Failed to fetch images for car $carId. Status Code: $errorCode, Error Body: $errorBody"
                )
                Result.failure(Exception("Failed to fetch images. Status Code: $errorCode, Error: $errorBody"))
            }
        } catch (e: Exception) {
            Log.e("CarRepository", "Exception when fetching images for car $carId", e)
            Result.failure(Exception("Exception when fetching images: ${e.message}", e))
        }
    }

    suspend fun uploadCarImage(carId: Int, file: File): Result<String> {
        return try {
            val requestBody = file.asRequestBody("image/*".toMediaTypeOrNull())
            val part = MultipartBody.Part.createFormData("image", file.name, requestBody)
            val response = apiService.uploadCarImage(carId, part)
            if (response.isSuccessful) {
                val responseBody = response.body()?.string()
                Log.d("CarRepository", "Upload response: $responseBody")
                if (responseBody.isNullOrEmpty()) {
                    Result.success("Image uploaded successfully")
                } else {
                    Result.success(responseBody)
                }
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e("CarRepository", "Upload failed: $errorBody")
                Result.failure(Exception("Failed to upload image: $errorBody"))
            }
        } catch (e: Exception) {
            Log.e("CarRepository", "Exception during upload", e)
            Result.failure(e)
        }
    }


    suspend fun registerCar(request: RegisterCarRequest): Result<String> {
        return try {
            val response = apiService.registerCar(request)
            if (response.isSuccessful) {
                val responseBody = response.body()?.string() ?: "Car registered successfully"
                Result.success(responseBody)
            } else {
                val errorBody = response.errorBody()?.string() ?: "Unknown error"
                Result.failure(Exception("Failed to register car: $errorBody"))
            }
        } catch (e: IOException) {
            Result.failure(Exception("Network error: ${e.message}", e))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun unregisterCar(carId: Int): Result<String> {
        return try {
            val response = apiService.unregisterCar(carId)
            if (response.isSuccessful) {
                val responseBody = response.message() ?: "Car unregistered successfully"
                Result.success(responseBody)
            } else {
                val errorBody = response.errorBody()?.string() ?: "Unknown error"
                Result.failure(Exception("Failed to unregister car: $errorBody"))
            }
        } catch (e: IOException) {
            Result.failure(Exception("Network error: ${e.message}", e))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun addCarLocation(locationRequest: LocationRequest): Result<Unit> {
        return try {
            Log.d("CarRepository", "Sending location request: $locationRequest")
            val response = apiService.addCarLocation(locationRequest)
            if (response.isSuccessful) {
                Log.d("CarRepository", "Location added successfully")
                Result.success(Unit)
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e(
                    "CarRepository",
                    "Failed to add car location. Status: ${response.code()}, Error: $errorBody"
                )
                Result.failure(Exception("Failed to add car location: $errorBody"))
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getBrands(): List<BrandResponse>? {
        return try {
            val response = apiService.getBrands()
            if (response.isSuccessful) {
                response.body()
            } else {
                Log.e(
                    "CarRepository",
                    "Failed to get brands: ${response.code()} ${response.message()}"
                )
                null
            }
        } catch (e: Exception) {
            Log.e("CarRepository", "Exception when getting brands", e)
            null
        }
    }

    suspend fun getModelsByBrand(brandId: Int): List<ModelResponse>? {
        return try {
            val response = apiService.getModelsByBrand(brandId)
            if (response.isSuccessful) {
                val models = response.body()
                Log.d("CarRepository", "Models received: ${models?.size}")
                models
            } else {
                Log.e(
                    "CarRepository",
                    "Failed to get models: ${response.code()} ${response.message()}"
                )
                null
            }
        } catch (e: Exception) {
            Log.e("CarRepository", "Exception when getting models", e)
            null
        }
    }
}

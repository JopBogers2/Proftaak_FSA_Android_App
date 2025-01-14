package com.example.rentmycar.repository

    import android.util.Log
    import com.example.rentmycar.api.ApiService
    import com.example.rentmycar.api.requests.CarResponse
    import com.example.rentmycar.api.requests.RegisterCarRequest
    import com.example.rentmycar.api.requests.ModelDTO
    import com.example.rentmycar.api.requests.BrandDTO
    import com.example.rentmycar.api.requests.CarDTO
    import com.example.rentmycar.api.requests.LocationRequest
    import com.example.rentmycar.api.responses.LocationResponse
    import com.squareup.moshi.Moshi
    import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
    import okhttp3.MediaType.Companion.toMediaTypeOrNull
    import okhttp3.MultipartBody
    import okhttp3.ResponseBody
    import okhttp3.RequestBody.Companion.toRequestBody
    import retrofit2.HttpException
    import java.io.IOException
    import javax.inject.Inject


    class CarRepository @Inject constructor(private val apiService: ApiService) {
        private val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()


    suspend fun getOwnerCars(): List<CarDTO> {
        val response = apiService.getOwnerCars()
        if (response.isSuccessful) {
            return response.body() ?: emptyList()
        } else {
            throw Exception("Failed to fetch owner cars: ${response.errorBody()?.string()}")
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

    suspend fun addCarLocation(carId: Int, latitude: Double, longitude: Double): Result<String> {
        return try {
            val request = LocationRequest(carId, latitude, longitude)
            val response = apiService.addCarLocation(request)
            if (response.isSuccessful) {
                Result.success("Location added successfully")
            } else {
                Result.failure(Exception("Failed to add location: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateCarLocation(carId: Int, latitude: Double, longitude: Double): Result<String> {
        return try {
            val request = LocationRequest(carId, latitude, longitude)
            val response = apiService.updateCarLocation(request)
            if (response.isSuccessful) {
                Result.success("Location updated successfully")
            } else {
                Result.failure(Exception("Failed to update location: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    suspend fun getBrands(): List<BrandDTO>? {
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

    suspend fun getModelsByBrand(brandId: Int): List<ModelDTO>? {
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

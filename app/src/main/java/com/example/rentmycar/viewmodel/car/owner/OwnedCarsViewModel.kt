package com.example.rentmycar.viewmodel.car.owner

import android.content.Context
import android.location.Location
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rentmycar.api.responses.OwnedCarResponse
import com.example.rentmycar.api.requests.LocationRequest
import com.example.rentmycar.repository.CarRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject
import java.io.FileOutputStream
import kotlinx.coroutines.withContext
import com.google.android.gms.location.LocationServices

import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource

@HiltViewModel
class OwnedCarsViewModel @Inject constructor(
    private val carRepository: CarRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _viewState = MutableStateFlow<UserCarsViewState>(UserCarsViewState.Loading)
    val viewState: StateFlow<UserCarsViewState> = _viewState

    private val _locationState = MutableStateFlow<LocationState>(LocationState.NoLocation)
    val locationState: StateFlow<LocationState> = _locationState

     private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    fun getUserCars() {
        viewModelScope.launch {
            _viewState.value = UserCarsViewState.Loading
            try {
                val cars = carRepository.getOwnerCars()
                if (cars.isEmpty()) {
                    _viewState.value = UserCarsViewState.NoCars
                } else {
                    _viewState.value = UserCarsViewState.Success(cars)
                }
            } catch (e: Exception) {
                _viewState.value = UserCarsViewState.Error(e.message ?: "Unknown error occurred")
            }
        }
    }

    fun setLocation(location: Location) {
        _locationState.value = LocationState.LocationAvailable(location)
    }


 private val _refreshTrigger = MutableStateFlow(0)
    val refreshTrigger: StateFlow<Int> = _refreshTrigger

    fun addCarLocation(carId: Int) {
        viewModelScope.launch {
            try {
                val cancellationTokenSource = CancellationTokenSource()

                fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, cancellationTokenSource.token)
                    .addOnSuccessListener { location ->
                        if (location != null) {
                            viewModelScope.launch {
                                val latitude = location.latitude
                                val longitude = location.longitude
                                Log.d("UserCarsViewModel", "Adding location for car $carId: $latitude, $longitude")
                                val locationRequest = LocationRequest(carId, latitude, longitude)
                                val result = carRepository.addCarLocation(locationRequest)
                                result.onSuccess {
                                    Log.d("UserCarsViewModel", "Location added successfully for car $carId")
                                    refreshCarList()
                                }.onFailure { error ->
                                    Log.e("UserCarsViewModel", "Failed to add location for car $carId", error)
                                    _viewState.value =
                                        UserCarsViewState.Error("Failed to add location: ${error.message}")
                                }
                            }
                        } else {
                            Log.e("UserCarsViewModel", "Location is null")
                            _viewState.value =
                                UserCarsViewState.Error("Failed to get current location")
                        }
                    }
                    .addOnFailureListener { e ->
                        Log.e("UserCarsViewModel", "Error getting location", e)
                        _viewState.value =
                            UserCarsViewState.Error("Error getting location: ${e.message}")
                    }
            } catch (e: Exception) {
                Log.e("UserCarsViewModel", "Error in addCarLocation", e)
                _viewState.value =
                    UserCarsViewState.Error("Error adding car location: ${e.message}")
            }
        }
    }

    fun refreshCarList() {
        _refreshTrigger.value += 1
        getUserCars()
    }



private val _carImages = MutableStateFlow<Map<Int, List<String>>>(emptyMap())
val carImages: StateFlow<Map<Int, List<String>>> = _carImages

fun uploadCarImage(carId: Int, imageUri: Uri, onComplete: (Boolean) -> Unit) {
    viewModelScope.launch {
        try {
            Log.d("UserCarsViewModel", "Starting image upload for car $carId")
            val file = getFileFromUri(imageUri)
            if (file != null) {
                Log.d("UserCarsViewModel", "File created successfully: ${file.absolutePath}")
                val result = carRepository.uploadCarImage(carId, file)
                result.onSuccess { message ->
                    Log.d("UserCarsViewModel", "Image upload successful. Message: $message")

                    getImagesByCar(carId)
                    onComplete(true)
                }.onFailure { error ->
                    Log.e("UserCarsViewModel", "Failed to upload image", error)
                    _viewState.value =
                        UserCarsViewState.Error("Failed to upload image: ${error.message}")
                    onComplete(false)
                }
            } else {
                Log.e("UserCarsViewModel", "Failed to create file from URI")
                _viewState.value = UserCarsViewState.Error("Failed to create file from URI")
                onComplete(false)
            }
        } catch (e: Exception) {
            Log.e("UserCarsViewModel", "Error uploading image", e)
            _viewState.value = UserCarsViewState.Error("Error uploading image: ${e.message}")
            onComplete(false)
        }
    }
}

private suspend fun getFileFromUri(uri: Uri): File? = withContext(Dispatchers.IO) {
    val fileName = getFileName(uri)
    Log.d("UserCarsViewModel", "Getting file from URI. Filename: $fileName")
    val tempFile = File(context.cacheDir, fileName)
    tempFile.createNewFile()
    try {
        val inputStream = context.contentResolver.openInputStream(uri)
        if (inputStream == null) {
            Log.e("UserCarsViewModel", "Failed to open input stream for URI")
            return@withContext null
        }
        val outputStream = FileOutputStream(tempFile)
        inputStream.use { input ->
            outputStream.use { output ->
                input.copyTo(output)
            }
        }
        Log.d("UserCarsViewModel", "File created successfully: ${tempFile.absolutePath}")
        tempFile
    } catch (e: Exception) {
        Log.e("UserCarsViewModel", "Error creating file from URI: ${e.message}", e)
        null
    }
}

private fun getFileName(uri: Uri): String {
    var fileName = "temp_image.jpg"
    context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
        val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        cursor.moveToFirst()
        fileName = cursor.getString(nameIndex)
    }
    return fileName
}

fun getImagesByCar(carId: Int) {
    viewModelScope.launch {
        try {
            Log.d("UserCarsViewModel", "Fetching images for car $carId")
            val result = carRepository.getImagesByCar(carId)
            result.onSuccess { images ->
                Log.d("UserCarsViewModel", "Fetched images for car $carId: $images")
                _carImages.update { currentMap ->
                    currentMap + (carId to images)
                }
            }.onFailure { error ->
                Log.e("UserCarsViewModel", "Error fetching images for car $carId: ${error.message}", error)

                _viewState.value =
                    UserCarsViewState.Error("Failed to fetch images for car $carId: ${error.message}")
            }
        } catch (e: Exception) {
            Log.e("UserCarsViewModel", "Exception in getImagesByCar for car $carId", e)
            _viewState.value =
                UserCarsViewState.Error("Exception fetching images for car $carId: ${e.message}")
        }
    }
}


    sealed class LocationState {
        object NoLocation : LocationState()
        data class LocationAvailable(val location: Location) : LocationState()
    }
}

sealed class UserCarsViewState {
    object Loading : UserCarsViewState()
    data class Success(val cars: List<OwnedCarResponse>) : UserCarsViewState()
    object NoCars : UserCarsViewState()
    data class Error(val message: String) : UserCarsViewState()
}
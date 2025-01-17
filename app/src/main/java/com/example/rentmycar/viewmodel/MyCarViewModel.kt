package com.example.rentmycar.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rentmycar.api.requests.BrandDTO
import com.example.rentmycar.api.requests.CarDTO
import com.example.rentmycar.api.requests.ModelDTO
import com.example.rentmycar.api.requests.RegisterCarRequest
import com.example.rentmycar.repository.CarRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyCarViewModel @Inject constructor(
    private val carRepository: CarRepository
) : ViewModel() {
    private val _registrationState = MutableStateFlow<RegistrationState>(RegistrationState.Idle)
    val registrationState: StateFlow<RegistrationState> = _registrationState

    private val _viewState = MutableStateFlow<MyCarViewState>(MyCarViewState.Loading)
    val viewState: StateFlow<MyCarViewState> = _viewState

    private val _brands = MutableStateFlow<List<BrandDTO>>(emptyList())
    val brands: StateFlow<List<BrandDTO>> = _brands.asStateFlow()

    private val _models = MutableStateFlow<List<ModelDTO>>(emptyList())
    val models: StateFlow<List<ModelDTO>> = _models.asStateFlow()

    private val _selectedBrandId = MutableStateFlow<Int?>(null)
    val selectedBrandId: StateFlow<Int?> = _selectedBrandId.asStateFlow()

    private val _selectedModelId = MutableStateFlow<Int?>(null)
    val selectedModelId: StateFlow<Int?> = _selectedModelId.asStateFlow()

    val selectedBrand: StateFlow<BrandDTO?> = _selectedBrandId.map { brandId ->
        _brands.value.find { it.id == brandId }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)

    val selectedModel: StateFlow<ModelDTO?> = _selectedModelId.map { modelId ->
        _models.value.find { it.id == modelId }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)

    private val _dataLoadingState = MutableStateFlow<DataLoadingState>(DataLoadingState.Initial)
    val dataLoadingState: StateFlow<DataLoadingState> = _dataLoadingState


    init {
        fetchBrands()
    }


    fun fetchBrands() {
        viewModelScope.launch {
            _dataLoadingState.value = DataLoadingState.Loading
            val brands = carRepository.getBrands()
            if (brands != null) {
                _brands.value = brands
                _dataLoadingState.value = DataLoadingState.Success
            } else {
                _dataLoadingState.value = DataLoadingState.Error("Failed to load brands")
            }
        }
    }

    fun selectBrand(brandId: Int) {
        _selectedBrandId.value = brandId
        fetchModels(brandId)
    }

    fun fetchModels(brandId: Int) {
        viewModelScope.launch {
            _dataLoadingState.value = DataLoadingState.Loading
            try {
                val models = carRepository.getModelsByBrand(brandId)
                _models.value = models ?: emptyList()
                _dataLoadingState.value = DataLoadingState.Success
            } catch (e: Exception) {
                _dataLoadingState.value =
                    DataLoadingState.Error("Failed to load models: ${e.message}")
            }
        }
    }


    fun selectModel(modelId: Int) {
        _selectedModelId.value = modelId
    }


    fun registerCar(request: RegisterCarRequest, onComplete: (Int?) -> Unit) {
        viewModelScope.launch {
            _registrationState.value = RegistrationState.Loading
            try {
                val result = carRepository.registerCar(request)
                result.fold(
                    onSuccess = { response ->
                        _registrationState.value = RegistrationState.Success(response)

                        onComplete(response.toIntOrNull())
                    },
                    onFailure = { error ->
                        _registrationState.value = RegistrationState.Error(
                            "Failed to register car: ${error.message}",
                            parseFieldErrors(error.message ?: "")
                        )
                        onComplete(null)
                    }
                )
            } catch (e: Exception) {
                _registrationState.value =
                    RegistrationState.Error("Unexpected error: ${e.message}", emptyMap())
                onComplete(null)
            }
        }
    }

    private fun parseFieldErrors(errorMessage: String): Map<String, String> {
        val fieldErrors = mutableMapOf<String, String>()
        if (errorMessage.contains("licensePlate")) fieldErrors["licensePlate"] =
            "License plate is required"
        if (errorMessage.contains("modelId")) fieldErrors["modelId"] = "Model ID is required"
        if (errorMessage.contains("fuel")) fieldErrors["fuel"] = "Fuel type is required"
        if (errorMessage.contains("year")) fieldErrors["year"] = "Year is required"
        if (errorMessage.contains("color")) fieldErrors["color"] = "Color is required"
        if (errorMessage.contains("transmission")) fieldErrors["transmission"] =
            "Transmission is required"
        if (errorMessage.contains("price")) fieldErrors["price"] = "Price is required"
        if (errorMessage.contains("category")) fieldErrors["category"] = "Category is required"
        return fieldErrors
    }
}

sealed class MyCarViewState {
    data object Loading : MyCarViewState()
    data class Success(val cars: List<CarDTO>) : MyCarViewState()
    data class Error(val message: String) : MyCarViewState()
}

sealed class RegistrationState {
    data object Idle : RegistrationState()
    data object Loading : RegistrationState()
    data class Success(val message: String) : RegistrationState()
    data class Error(val message: String, val fieldErrors: Map<String, String> = emptyMap()) :
        RegistrationState()
}

sealed class DataLoadingState {
    data object Initial : DataLoadingState()
    data object Loading : DataLoadingState()
    data object Success : DataLoadingState()
    data class Error(val message: String) : DataLoadingState()
}







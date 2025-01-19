package com.example.rentmycar.viewmodel.car.owner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rentmycar.api.responses.BrandResponse
import com.example.rentmycar.api.responses.OwnedCarResponse
import com.example.rentmycar.api.responses.ModelResponse
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
class OwnedCarViewModel @Inject constructor(
    private val carRepository: CarRepository
) : ViewModel() {
    private val _registrationState = MutableStateFlow<RegistrationState>(RegistrationState.Idle)
    val registrationState: StateFlow<RegistrationState> = _registrationState

    private val _viewState = MutableStateFlow<OwnedCarViewState>(OwnedCarViewState.Loading)
    val viewState: StateFlow<OwnedCarViewState> = _viewState

    private val _brands = MutableStateFlow<List<BrandResponse>>(emptyList())
    val brands: StateFlow<List<BrandResponse>> = _brands.asStateFlow()

    private val _models = MutableStateFlow<List<ModelResponse>>(emptyList())
    val models: StateFlow<List<ModelResponse>> = _models.asStateFlow()

    private val _selectedBrandId = MutableStateFlow<Int?>(null)
    val selectedBrandId: StateFlow<Int?> = _selectedBrandId.asStateFlow()

    private val _selectedModelId = MutableStateFlow<Int?>(null)
    val selectedModelId: StateFlow<Int?> = _selectedModelId.asStateFlow()

    val selectedBrand: StateFlow<BrandResponse?> = _selectedBrandId.map { brandId ->
        _brands.value.find { it.id == brandId }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)

    val selectedModel: StateFlow<ModelResponse?> = _selectedModelId.map { modelId ->
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

sealed class OwnedCarViewState {
    data object Loading : OwnedCarViewState()
    data class Success(val cars: List<OwnedCarResponse>) : OwnedCarViewState()
    data class Error(val message: String) : OwnedCarViewState()
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







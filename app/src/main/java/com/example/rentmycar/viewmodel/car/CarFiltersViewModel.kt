package com.example.rentmycar.viewmodel.car

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class CarFiltersViewModel : ViewModel() {
    private val _filters = MutableStateFlow<Map<String, String>>(emptyMap())
    val filters: StateFlow<Map<String, String>> get() = _filters

    fun addOrRemoveFilter(key: String, value: String?) {
        _filters.value = _filters.value.toMutableMap().apply {
            if (value.isNullOrEmpty()) remove(key) else this[key] = value
        }
    }
}
package com.example.rentmycar.api.requests


data class UpdateCarRequest(
    val carId: Int,
    val year: Int? = null,
    val color: String? = null,
    val transmission: String? = null,
    val fuel: String? = null,
    val price: Double? = null,
)
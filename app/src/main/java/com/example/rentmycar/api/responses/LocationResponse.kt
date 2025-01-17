package com.example.rentmycar.api.responses

data class LocationResponse(
    val id: Int,
    val carId: Int,
    val latitude: Double,
    val longitude: Double
)
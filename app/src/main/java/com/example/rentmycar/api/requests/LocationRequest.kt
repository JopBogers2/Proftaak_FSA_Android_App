package com.example.rentmycar.api.requests

data class LocationRequest(
    val carId: Int,
    val latitude: Double,
    val longitude: Double
)
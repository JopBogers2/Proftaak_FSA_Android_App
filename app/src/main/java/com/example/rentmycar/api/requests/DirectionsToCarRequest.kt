package com.example.rentmycar.api.requests

data class DirectionsToCarRequest (
    val latitude: Double,
    val longitude: Double,
    val carId: Int
)
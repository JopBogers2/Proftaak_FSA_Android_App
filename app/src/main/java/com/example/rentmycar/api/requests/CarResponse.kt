package com.example.rentmycar.api.requests

data class CarResponse(
    val id: Int,
    val ownerId: Int,
    val locationId: Int?, // Nullable because it can be null
    val model: String,
    val licensePlate: String,
    val fuel: String,
    val year: Int,
    val color: String,
    val transmission: String,
    val price: Double,
    val category: String
)
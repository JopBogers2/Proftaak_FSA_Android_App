package com.example.rentmycar.api.requests

data class CarDTO(
    val id: Int,
    val ownerId: Int,
    val locationId: Int?,
    val model: String,
    val licensePlate: String,
    val fuel: String,
    val year: Int,
    val color: String,
    val transmission: String,
    val price: Double,
    val category: String,

)
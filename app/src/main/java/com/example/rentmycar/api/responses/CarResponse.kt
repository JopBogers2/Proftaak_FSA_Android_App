package com.example.rentmycar.api.responses

data class CarResponse(
    val id: Int,
    val ownerId: Int,
    val ownerName: String,
    val ownerEmail: String,
    val model: String,
    val brand: String,
    val locationId: Int?,
    val licensePlate: String,
    val year: Int,
    val color: String,
    val price: Double,
    val transmission: String,
    val fuel: String,
    val category: String,
)
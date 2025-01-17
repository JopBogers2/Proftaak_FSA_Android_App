package com.example.rentmycar.api.requests

data class RegisterCarRequest(
    val licensePlate: String,
    val modelId: Int,
    val fuel: String,
    val year: Int,
    val color: String,
    val transmission: String,
    val price: Double,
    val image: ByteArray? = null,

)
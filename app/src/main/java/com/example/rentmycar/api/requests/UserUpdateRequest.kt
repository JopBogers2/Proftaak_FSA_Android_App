package com.example.rentmycar.api.requests

data class UserUpdateRequest(
    val firstName: String,
    val lastName: String,
    val username: String,
    val email: String,
    val password: String
)
package com.example.rentmycar.api.requests

data class UserResponse(
    val firstName : String,
    val lastName : String,
    val username : String,
    val email : String,
    val score : Int
)
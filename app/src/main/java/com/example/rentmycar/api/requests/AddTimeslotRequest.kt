package com.example.rentmycar.api.requests

import kotlinx.datetime.LocalDateTime

data class AddTimeslotRequest(
    val carId: Int,
    val availableFrom: LocalDateTime,
    val availableUntil: LocalDateTime,
)
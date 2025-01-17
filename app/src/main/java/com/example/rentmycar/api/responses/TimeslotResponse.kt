package com.example.rentmycar.api.responses

import kotlinx.datetime.LocalDateTime

data class TimeslotResponse(
    val id: Int,
    val carId: Int,
    val availableFrom: LocalDateTime,
    val availableUntil: LocalDateTime,
)
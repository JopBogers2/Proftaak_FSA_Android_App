package com.example.rentmycar.api.responses

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class DirectionsToCarResponse(
    val routes: List<Route>
)

@JsonClass(generateAdapter = true)
data class Route(
    val legs: List<Leg>
)

@JsonClass(generateAdapter = true)
data class Leg(
    val start_location: Location,
    val end_location: Location,
    val steps: List<Step>
)

@JsonClass(generateAdapter = true)
data class Step(
    @Json(name = "start_location") val startLocation: Location,
    @Json(name = "end_location") val endLocation: Location,
    @Json(name = "html_instructions") val htmlInstructions: String
)

@JsonClass(generateAdapter = true)
data class Location(
    val lat: Double,
    val lng: Double
)
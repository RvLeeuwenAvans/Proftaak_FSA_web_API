package com.rentmycar.dtos.requests.location

import com.rentmycar.services.exceptions.RequestValidationException
import kotlinx.serialization.Serializable

@Serializable
data class LocationRequest (
    val carId: Int,
    val longitude: Double,
    val latitude: Double,
) {
    fun validate() {
        val errors = mutableListOf<String>()

        if (carId <= 0) errors.add("Car ID is invalid")
        if (longitude !in -90.0..90.0) errors.add("Longitude is invalid")
        if (latitude !in -90.0..90.0) errors.add("Latitude is invalid")

        if (errors.isNotEmpty()) {
            throw RequestValidationException(errors)
        }
    }
}
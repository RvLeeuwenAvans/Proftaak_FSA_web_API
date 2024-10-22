package com.rentmycar.requests.car

import com.rentmycar.services.exceptions.RequestValidationException
import kotlinx.serialization.Serializable

@Serializable
data class DirectionsToCarRequest(
    val latitude: Double,
    val longitude: Double,
    val carId: Int,
) {
    fun validate() {
        val errors = mutableListOf<String>()

        if (latitude !in -90.0..90.0) errors.add("Latitude must be between -90 and 90")
        if (longitude !in -180.0..180.0) errors.add("Longitude must be between -180 and 180")
        if (carId < 0) errors.add("Car ID is invalid")

        if (errors.isNotEmpty()) {
            throw RequestValidationException(errors)
        }
    }
}

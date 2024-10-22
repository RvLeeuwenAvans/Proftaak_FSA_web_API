package com.rentmycar.requests.reservation

import com.rentmycar.services.exceptions.RequestValidationException
import kotlinx.serialization.Serializable

@Serializable
data class RemoveReservationRequest(
    val reservationId: Int
) {
    fun validate() {
        val errors = mutableListOf<String>()

        if (reservationId <= 0) errors.add("No reservation Id given")

        if (errors.isNotEmpty()) {
            throw RequestValidationException(errors)
        }
    }
}
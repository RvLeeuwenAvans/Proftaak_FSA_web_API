package com.rentmycar.dtos.requests.reservation

import com.rentmycar.services.exceptions.RequestValidationException
import kotlinx.serialization.Serializable

@Serializable
data class CreateReservationRequest(
    val timeslotId: Int
) {
    fun validate() {
        val errors = mutableListOf<String>()

        if (timeslotId == 0) errors.add("No timeslot Id given")

        if (errors.isNotEmpty()) {
            throw RequestValidationException(errors)
        }
    }
}
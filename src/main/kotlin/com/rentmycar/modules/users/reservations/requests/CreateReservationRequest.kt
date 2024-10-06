package com.rentmycar.modules.users.reservations.requests

import kotlinx.serialization.Serializable

@Serializable
data class CreateReservationRequest(
    val timeslotId: Int
) {
    fun validate(): List<String> {
        val errors = mutableListOf<String>()

        if (timeslotId == 0) errors.add("No timeslot Id given")

        return errors
    }
}
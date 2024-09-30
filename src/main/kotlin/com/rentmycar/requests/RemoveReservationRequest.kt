package com.rentmycar.requests

import kotlinx.serialization.Serializable

@Serializable
data class RemoveReservationRequest(
    val reservationId: Int
) {
    fun validate(): List<String> {
        val errors = mutableListOf<String>()

        if (reservationId == 0) errors.add("No reservation Id given")

        return errors
    }
}
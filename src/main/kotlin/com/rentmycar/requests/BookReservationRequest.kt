package com.rentmycar.requests

import kotlinx.serialization.Serializable

@Serializable
data class BookReservationRequest(
    val userId: Int,
    val licensePlate: String
) {
    fun validate(): List<String> {
        val errors = mutableListOf<String>()

        if (licensePlate.isEmpty()) errors.add("License plate name cannot be empty")

        return errors
    }
}
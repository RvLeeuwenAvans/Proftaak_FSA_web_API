package com.rentmycar.requests

import kotlinx.serialization.Serializable

@Serializable
data class RegisterCarRequest(
    val licensePlate: String
) {
    fun validate(): List<String> {
        val errors = mutableListOf<String>()

        if (licensePlate.isEmpty()) errors.add("License plate name cannot be empty")

        return errors
    }
}
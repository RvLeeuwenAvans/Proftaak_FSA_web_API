package com.rentmycar.requests

import kotlinx.serialization.Serializable

@Serializable
data class RegisterCarRequest(
    val userId: Int,
    val licenseplate: String
) {
    fun validate(): List<String> {
        val errors = mutableListOf<String>()

        if (licenseplate.isEmpty()) errors.add("License plate name cannot be empty")

        return errors
    }
}
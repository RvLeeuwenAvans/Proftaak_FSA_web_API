package com.rentmycar.requests.car

import kotlinx.serialization.Serializable

@Serializable


data class RegisterCarRequest(
    val licensePlate: String,
    val modelId: Int,
    val fuelId: Int,
    val year: Int,
    val color: String,
    val transmission: String
) {
    // Optional: You can add validation logic here
    fun validate(): List<String> {
        val errors = mutableListOf<String>()

        if (licensePlate.isBlank()) errors.add("License plate cannot be blank")
        if (year <= 0) errors.add("Invalid year")
        if (color.isBlank()) errors.add("Color cannot be blank")
        if (transmission.isBlank()) errors.add("Transmission cannot be blank")

        return errors
    }
}

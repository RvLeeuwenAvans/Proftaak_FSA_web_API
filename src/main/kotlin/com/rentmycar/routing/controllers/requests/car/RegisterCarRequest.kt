package com.rentmycar.routing.controllers.requests.car

import com.rentmycar.services.exceptions.RequestValidationException
import com.rentmycar.utils.FuelType.Companion.fuelTypes
import com.rentmycar.utils.Transmission.Companion.transmissions
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class RegisterCarRequest(
    val licensePlate: String,
    val modelId: Int,
    val fuel: String,
    val year: Int,
    val color: String,
    val transmission: String,
    val price: Double?,
) {
    fun validate() {
        val errors = mutableListOf<String>()

        if (licensePlate.isBlank()) errors.add("License plate cannot be blank")

        if (modelId < 0) errors.add("Model ID is invalid")

        if (!fuelTypes.contains(fuel.uppercase())) errors.add("Fuel is invalid")

        val calendar = Calendar.getInstance()
        val currentYear = calendar.get(Calendar.YEAR).toInt()
        if (year < 1886 || year > currentYear) errors.add("Year must be between 1886 and $currentYear")

        if (color.isBlank()) errors.add("Color cannot be blank")

        if (!transmissions.contains(transmission.uppercase()))
            errors.add("Transmission is invalid")

        if (price != null && price < 0.0) errors.add("Price must be a non-negative number")

        if (errors.isNotEmpty()) {
            throw RequestValidationException(errors)
        }
    }
}

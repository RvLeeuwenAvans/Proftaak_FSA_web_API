package com.rentmycar.routing.controllers.requests.car

import com.rentmycar.services.exceptions.RequestValidationException
import com.rentmycar.utils.FuelType.Companion.fuelTypes
import com.rentmycar.utils.Transmission.Companion.transmissions
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class UpdateCarRequest(
    val carId: Int,
    val year: Int? = null,
    val color: String? = null,
    val transmission: String? = null,
    val fuel: String? = null,
    val price: Double? = null,
) {
    fun validate() {
        val errors = mutableListOf<String>()

        if (carId < 0) errors.add("Car ID is invalid")

        if (color != null && color.isBlank()) errors.add("Color cannot be blank")

        val calendar = Calendar.getInstance()
        val currentYear = calendar.get(Calendar.YEAR).toInt()
        if (year != null && (year < 1886 || year > currentYear)) errors.add("Year must be between 1886 and $currentYear")

        if (fuel != null && !fuelTypes.contains(fuel.uppercase()))
            errors.add("Fuel is invalid")
        if (transmission != null && !transmissions.contains(transmission.uppercase()))
            errors.add("Transmission is invalid")

        if (price != null && price < 0.0) errors.add("Price must be a non-negative number")

        if (errors.isNotEmpty()) {
            throw RequestValidationException(errors)
        }
    }
}

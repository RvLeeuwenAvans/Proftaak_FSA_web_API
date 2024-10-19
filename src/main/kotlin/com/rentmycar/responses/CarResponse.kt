package com.rentmycar.responses

import com.rentmycar.utils.FuelType
import com.rentmycar.utils.Transmission
import kotlinx.serialization.Serializable

@Serializable
data class CarDTO (
    val id: Int,
    val ownerId: Int,
    val licensePlate: String,
    val model: String,
    val fuel: FuelType,
    val year: Int,
    val color: String,
    val transmission: Transmission,
    val price: Double,
)
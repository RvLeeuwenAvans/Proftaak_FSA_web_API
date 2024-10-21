package com.rentmycar.routing.controllers.responses

import com.rentmycar.utils.Category
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
    val category: Category,
)
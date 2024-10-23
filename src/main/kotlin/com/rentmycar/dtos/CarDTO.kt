package com.rentmycar.dtos

import com.rentmycar.utils.Category
import com.rentmycar.utils.FuelType
import com.rentmycar.utils.Transmission
import kotlinx.serialization.Serializable

@Serializable
data class CarDTO(
    val id: Int,
    val ownerId: Int,
    val locationId: Int? = null,
    val model: String,
    val licensePlate: String,
    val fuel: FuelType,
    val year: Int,
    val color: String,
    val transmission: Transmission,
    val price: Double,
    val category: Category,
)
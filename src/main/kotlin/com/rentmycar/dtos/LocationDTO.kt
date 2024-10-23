package com.rentmycar.dtos

import kotlinx.serialization.Serializable

@Serializable
data class LocationDTO(
    val id: Int,
    val carId: Int? = null,
    val longitude: Double,
    val latitude: Double,
)
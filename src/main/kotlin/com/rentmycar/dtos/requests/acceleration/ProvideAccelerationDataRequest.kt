package com.rentmycar.dtos.requests.acceleration

import kotlinx.serialization.Serializable

@Serializable
data class ProvideAccelerationDataRequest(
    val ax: Float,
    val ay: Float,
    val az: Float,
)
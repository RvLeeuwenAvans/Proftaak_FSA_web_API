package com.rentmycar.dtos

import kotlinx.serialization.Serializable

@Serializable
data class ReservationDTO(
    val id: Int,
    val reservorId: Int,
    val timeslotId: Int,
    val averageAcceleration: Double?,
    val distance: Double?,
    val score: Int?
)
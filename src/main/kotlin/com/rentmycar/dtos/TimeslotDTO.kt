package com.rentmycar.dtos

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class TimeslotDTO(
    val id: Int,
    val carId: Int,
    val availableFrom: LocalDateTime,
    val availableUntil: LocalDateTime,
)
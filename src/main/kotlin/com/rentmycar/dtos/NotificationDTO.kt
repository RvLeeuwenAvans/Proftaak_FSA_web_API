package com.rentmycar.dtos

import kotlinx.serialization.Serializable

@Serializable
data class NotificationDTO(
    val id: Int,
    val userId: Int,
    val subject: String,
    val message: String,
    val timestamp: kotlinx.datetime.Instant
)
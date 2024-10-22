package com.rentmycar.dtos

import java.time.LocalDateTime

data class NotificationDTO(
    val id: Long,
    val userId: Long,
    val message: String,
    val timestamp: LocalDateTime = LocalDateTime.now()
)
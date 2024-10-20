package com.rentmycar.entities

import java.time.LocalDateTime

data class Notification(
    val id: Long,
    val userId: Long,
    val message: String,
    val timestamp: LocalDateTime = LocalDateTime.now()
)
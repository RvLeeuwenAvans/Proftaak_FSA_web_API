package com.rentmycar.dtos

import com.rentmycar.utils.UserRole
import kotlinx.serialization.Serializable

@Serializable
data class UserDTO(
    val id: Int,
    val firstName: String,
    val lastName: String,
    val username: String,
    val email: String,
    val role: UserRole,
    val score: Int = 0,
)

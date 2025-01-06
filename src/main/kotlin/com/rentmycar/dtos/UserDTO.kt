package com.rentmycar.dtos

import com.rentmycar.entities.Users.default
import com.rentmycar.entities.Users.enumerationByName
import com.rentmycar.entities.Users.integer
import com.rentmycar.entities.Users.uniqueIndex
import com.rentmycar.entities.Users.varchar
import com.rentmycar.utils.UserRole
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class UserDTO(
    val id: Int,
    val firstName: String,
    val lastName: String,
    val username: String,
    val email: String,
    val score: Int,
)
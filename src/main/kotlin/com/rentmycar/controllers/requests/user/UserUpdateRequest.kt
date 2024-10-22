package com.rentmycar.controllers.requests.user

import kotlinx.serialization.Serializable

@Serializable
data class UserUpdateRequest(
    val firstName: String? = null,
    val lastName: String? = null,
    val username: String? = null,
    val email: String? = null,
    val password: String? = null,
    val role: String? = null
)
package com.rentmycar.dtos.requests.user

import com.rentmycar.services.exceptions.RequestValidationException
import kotlinx.serialization.Serializable

@Serializable
data class UserUpdateRequest(
    val firstName: String? = null,
    val lastName: String? = null,
    val username: String? = null,
    val email: String? = null,
    val password: String? = null,
) {
    fun validate() {
        val errors = mutableListOf<String>()

        if (firstName != null && firstName.isBlank()) errors.add("First name cannot be empty")
        if (lastName != null && lastName.isBlank()) errors.add("Last name cannot be empty")
        if (username!= null && username.isBlank()) errors.add("Username cannot be empty")
        if (email != null && !email.contains("@")) errors.add("Email must be valid")
        if (password != null && password.length < 6) errors.add("Password must be at least 6 characters long")

        if (errors.isNotEmpty()) {
            throw RequestValidationException(errors)
        }
    }
}
package com.rentmycar.routing.controllers.requests.user

import com.rentmycar.services.exceptions.RequestValidationException
import kotlinx.serialization.Serializable

@Serializable
data class UserLoginRequest(
    val email: String,
    val password: String
) {
    fun validate() {
        val errors = mutableListOf<String>()

        if (email.isBlank() || !email.contains("@")) {
            errors.add("Email must be valid")
        }

        if (password.isBlank()) {
            errors.add("Password cannot be empty")
        }

        if (errors.isNotEmpty()) {
            throw RequestValidationException(errors)
        }
    }
}

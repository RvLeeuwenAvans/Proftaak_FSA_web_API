package com.rentmycar.modules.users.requests

import kotlinx.serialization.Serializable

@Serializable
data class UserLoginRequest(
    val email: String,
    val password: String
) {
    fun validate(): List<String> {
        val errors = mutableListOf<String>()

        if (email.isBlank() || !email.contains("@")) {
            errors.add("Email must be valid")
        }

        if (password.isBlank()) {
            errors.add("Password cannot be empty")
        }

        return errors
    }
}

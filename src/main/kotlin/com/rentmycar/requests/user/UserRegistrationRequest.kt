package com.rentmycar.requests.user

import com.rentmycar.utils.UserRole
import kotlinx.serialization.Serializable

@Serializable
data class UserRegistrationRequest(
    val firstName: String,
    val lastName: String,
    val username: String,
    val email: String,
    val password: String,
) {
    // TODO: update this function if needed.
    fun generateRole(): UserRole =
        if (this.email.endsWith("@student.avans.nl")) UserRole.ADMIN else UserRole.DEFAULT

    fun validate(): List<String> {
        val errors = mutableListOf<String>()

        if (firstName.isBlank()) errors.add("First name cannot be empty")
        if (lastName.isBlank()) errors.add("Last name cannot be empty")
        if (username.isBlank()) errors.add("Username cannot be empty")
        if (!email.contains("@")) errors.add("Email must be valid")
        if (password.length < 6) errors.add("Password must be at least 6 characters long")

        return errors
    }
}
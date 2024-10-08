package com.rentmycar.controllers

import com.auth0.jwt.JWT
import com.rentmycar.authentication.JWTConfig
import com.rentmycar.authentication.PasswordHasher
import com.rentmycar.plugins.user
import com.rentmycar.repositories.UserRepository
import com.rentmycar.requests.user.UserLoginRequest
import com.rentmycar.requests.user.UserRegistrationRequest
import com.rentmycar.requests.user.UserUpdateRequest
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import java.util.*

class UserController(private val config: JWTConfig) {

    private val userRepository = UserRepository()

    suspend fun registerUser(call: ApplicationCall) {

        val registrationRequest = call.receive<UserRegistrationRequest>()
        val validationErrors = registrationRequest.validate()

        if (validationErrors.isNotEmpty()) return call.respond(
            HttpStatusCode.BadRequest,
            "Invalid registration data: ${validationErrors.joinToString(", ")}"
        )

        if (userRepository.doesUserExistByEmail(registrationRequest.email)) return call.respond(
            HttpStatusCode.Conflict,
            "User with this email already exists"
        )

        if (userRepository.doesUserExistByUsername(registrationRequest.username)) return call.respond(
            HttpStatusCode.Conflict,
            "User with this username already exists"
        )

        userRepository.createUser(registrationRequest)
        call.respond(HttpStatusCode.OK, "User registered successfully")
    }

    suspend fun loginUser(call: ApplicationCall) {
        val loginRequest = call.receive<UserLoginRequest>()
        val validationErrors = loginRequest.validate()

        if (validationErrors.isNotEmpty()) return call.respond(
            HttpStatusCode.BadRequest,
            "Invalid login data: ${validationErrors.joinToString(", ")}"
        )

        val user = userRepository.getUserByEmail(loginRequest.email) ?: return call.respond(
            HttpStatusCode.Unauthorized,
            "Invalid credentials"
        )

        if (PasswordHasher.verifyPassword(loginRequest.password, user.password)) {
            val token = JWT.create()
                .withAudience(config.audience)
                .withIssuer(config.issuer)
                .withClaim("email", user.email)
                .withClaim("id", user.id.value)
                .withExpiresAt(Date(System.currentTimeMillis() + 3600000))
                .sign(config.algorithm)
            call.respond(mapOf("token" to token))
        } else {
            call.respond(HttpStatusCode.Unauthorized, "Invalid credentials")
        }
    }

    /**
     * updates the currently authenticated user, retrieving the user via JWT token.
     */
    suspend fun updateUser(call: ApplicationCall) {
        val updateRequest = call.receive<UserUpdateRequest>()
        val user = call.user()

        userRepository.updateUser(user, updateRequest)

        call.respond(HttpStatusCode.OK, "User updated successfully")
    }

    /**
     * removes the currently authenticated user, retrieving the user via JWT token.
     */
    suspend fun deleteUser(call: ApplicationCall) {
        val user = call.user()

        userRepository.deleteUser(user)

        call.respond(HttpStatusCode.OK, "User deleted successfully")
    }
}
package com.rentmycar.controllers

import com.auth0.jwt.JWT
import com.rentmycar.authentication.JWTConfig
import com.rentmycar.authentication.PasswordHasher
import com.rentmycar.repositories.UserRepository
import com.rentmycar.requests.user.LoginRequest
import com.rentmycar.requests.user.RegistrationRequest
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*

class UserController(private val config: JWTConfig) {

    private val userRepository = UserRepository()

    suspend fun registerUser(call: ApplicationCall) {

        val registrationRequest = call.receive<RegistrationRequest>()
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

    suspend fun login(call: ApplicationCall) {
        val loginRequest = call.receive<LoginRequest>()
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
                .sign(config.algorithm)
            call.respond(mapOf("token" to token))
        } else {
            call.respond(HttpStatusCode.Unauthorized, "Invalid credentials")
        }
    }
}
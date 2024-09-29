package com.rentmycar.controllers

import com.auth0.jwt.JWT
import com.rentmycar.authentication.JWTConfig
import com.rentmycar.authentication.PasswordHasher
import com.rentmycar.repositories.UserRepository
import com.rentmycar.requests.LoginRequest
import com.rentmycar.requests.RegistrationRequest
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*

class UserController(private val config: JWTConfig) {

    private val userRepository = UserRepository()

    suspend fun register(call: ApplicationCall) {

        val registrationRequest = call.receive<RegistrationRequest>()
        val validationErrors = registrationRequest.validate()

        if (validationErrors.isNotEmpty()) {
            call.respond(HttpStatusCode.BadRequest, "Invalid registration data: ${validationErrors.joinToString(", ")}")
            return
        }

        if (userRepository.doesUserExistByEmail(registrationRequest.email)) {
            call.respond(HttpStatusCode.Conflict, "User with this email already exists")
            return
        }

        if (userRepository.doesUserExistByUsername(registrationRequest.username)) {
            call.respond(HttpStatusCode.Conflict, "User with this username already exists")
            return
        }

        userRepository.createUser(registrationRequest)

        call.respond(HttpStatusCode.OK, "User registered successfully")
    }

    suspend fun login(call: ApplicationCall) {
        val loginRequest = call.receive<LoginRequest>()
        val validationErrors = loginRequest.validate()

        if (validationErrors.isNotEmpty()) {
            call.respond(HttpStatusCode.BadRequest, "Invalid login data: ${validationErrors.joinToString(", ")}")
            return
        }

        val user = userRepository.getUserByEmail(loginRequest.email)

        if (user != null && PasswordHasher.verifyPassword(loginRequest.password, user.password)) {
            val token = JWT.create()
                .withAudience(config.audience)
                .withIssuer(config.issuer)
                .withClaim("email", user.email)
                .sign(config.algorithm)
            call.respond(mapOf("token" to token))
        } else {
            call.respond( HttpStatusCode.Unauthorized, "Invalid credentials")
        }
    }
}
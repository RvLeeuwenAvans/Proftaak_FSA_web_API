package com.rentmycar.controllers

import com.auth0.jwt.JWT
import com.rentmycar.authentication.JWTConfig
import com.rentmycar.authentication.PasswordHasher
import com.rentmycar.entities.User
import com.rentmycar.entities.Users
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import org.jetbrains.exposed.sql.transactions.transaction

class UserController(private val config: JWTConfig) {

    suspend fun register(call: ApplicationCall) {
        val registrationRequest = call.receive<RegistrationRequest>()

        val hashedPassword = PasswordHasher.hashPassword(registrationRequest.password)

        transaction {
            User.new {
                firstName = registrationRequest.firstName
                lastName = registrationRequest.lastName
                username = registrationRequest.username
                email = registrationRequest.email
                password = hashedPassword
            }
        }

        call.respondText("User registered successfully")
    }

    suspend fun login(call: ApplicationCall) {
        val loginRequest = call.receive<LoginRequest>()

        val user = transaction {
            User.find { Users.email eq loginRequest.email }.singleOrNull()
        }

        if (user != null && PasswordHasher.verifyPassword(loginRequest.password, user.password)) {
            val token = JWT.create()
                .withAudience(config.audience)
                .withIssuer(config.issuer)
                .withClaim("email", user.email)
                .sign(config.algorithm)
            call.respond(mapOf("token" to token))
        } else {
            call.respondText("Invalid credentials", status = HttpStatusCode.Unauthorized)
        }
    }
}

data class RegistrationRequest(
    val firstName: String,
    val lastName: String,
    val username: String,
    val email: String,
    val password: String
)

data class LoginRequest(val email: String, val password: String)
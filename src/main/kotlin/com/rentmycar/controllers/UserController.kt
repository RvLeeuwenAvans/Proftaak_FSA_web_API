package com.rentmycar.controllers

import com.auth0.jwt.JWT
import com.rentmycar.authentication.JWTConfig
import com.rentmycar.authentication.PasswordHasher
import com.rentmycar.plugins.user
import com.rentmycar.dtos.requests.user.UserLoginRequest
import com.rentmycar.dtos.requests.user.UserRegistrationRequest
import com.rentmycar.dtos.requests.user.UserUpdateRequest
import com.rentmycar.services.UserService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import java.util.*
import javax.naming.AuthenticationException

class UserController(private val config: JWTConfig) {
    private val userService = UserService()

    suspend fun registerUser(call: ApplicationCall) {
        val registrationRequest = call.receive<UserRegistrationRequest>()
        registrationRequest.validate()

        userService.create(registrationRequest, registrationRequest.generateRole())

        call.respond(HttpStatusCode.OK, "User registered successfully")
    }

    suspend fun loginUser(call: ApplicationCall) {
        val loginRequest = call.receive<UserLoginRequest>()
        loginRequest.validate()

        val user = userService.getByEmail(loginRequest.email)

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
            throw AuthenticationException("Password is not correct")
        }
    }

    suspend fun updateUser(call: ApplicationCall) {
        val updateRequest = call.receive<UserUpdateRequest>()
        val user = call.user()

        userService.update(user, updateRequest)

        call.respond(HttpStatusCode.OK, "User updated successfully")
    }

    suspend fun deleteUser(call: ApplicationCall) {
        val user = call.user()

        userService.delete(user)

        call.respond(HttpStatusCode.OK, "User deleted successfully")
    }

    suspend fun getScore(call: ApplicationCall) {
        val user = call.user()

        call.respond(HttpStatusCode.OK, mapOf("score" to user.score))
    }
}
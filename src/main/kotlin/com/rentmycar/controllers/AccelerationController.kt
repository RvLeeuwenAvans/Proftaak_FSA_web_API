package com.rentmycar.controllers

import com.rentmycar.dtos.requests.acceleration.ProvideAccelerationDataRequest
import com.rentmycar.plugins.user
import com.rentmycar.services.AccelerationService
import com.rentmycar.services.UserService
import com.rentmycar.services.exceptions.RequestValidationException
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.http.*
import io.ktor.server.request.receive

class AccelerationController {
    private val accelerationService = AccelerationService()

    suspend fun provideAccelerationData(call: ApplicationCall) {
        val request = call.receive<ProvideAccelerationDataRequest>()
        val user = call.user()

        UserService().updateUserScore(
            user, accelerationService.getScore(
                request.ax,
                request.ay,
                request.az
            )
        )
        call.respond(HttpStatusCode.OK, mapOf("message" to "Acceleration data successfully received."))
    }

    suspend fun calculateVelocity(call: ApplicationCall) {
        val errors = mutableListOf<String>()
        val initialVelocity = call.parameters["initialVelocity"]?.toDoubleOrNull()
            .also { if (it == null) errors.add("Invalid initialVelocity") }
        val acceleration = call.parameters["acceleration"]?.toDoubleOrNull()
            .also { if (it == null) errors.add("Invalid acceleration") }
        val deltaTime = call.parameters["deltaTime"]?.toDoubleOrNull()
            .also { if (it == null) errors.add("Invalid deltaTime") }

        if (initialVelocity == null || acceleration == null || deltaTime == null)
            throw RequestValidationException(errors)

        val velocity = accelerationService.calculateVelocity(initialVelocity, acceleration, deltaTime)
        call.respond(HttpStatusCode.OK, "Velocity: $velocity")
    }
}
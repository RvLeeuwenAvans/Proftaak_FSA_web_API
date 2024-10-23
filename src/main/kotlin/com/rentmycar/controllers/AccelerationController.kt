package com.rentmycar.controllers

import com.rentmycar.services.AccelerationService
import com.rentmycar.services.exceptions.RequestValidationException
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.http.*

class AccelerationController {
    private val accelerationService = AccelerationService()

    suspend fun calculateAcceleration(call: ApplicationCall) {
        val errors = mutableListOf<String>()
        val ax = call.parameters["ax"]?.toDoubleOrNull().also { if (it == null) errors.add("Invalid ax vector") }
        val ay = call.parameters["ay"]?.toDoubleOrNull().also { if (it == null) errors.add("Invalid ay vector") }
        val az = call.parameters["az"]?.toDoubleOrNull().also { if (it == null) errors.add("Invalid az vector") }

        if (ax == null || ay == null || az == null)
            throw RequestValidationException(errors)

        val magnitude = accelerationService.calculateAccelerationMagnitude(ax, ay, az)
        call.respond(HttpStatusCode.OK, "Acceleration Magnitude: $magnitude")
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
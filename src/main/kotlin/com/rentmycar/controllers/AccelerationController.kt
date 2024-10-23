package com.rentmycar.controllers

import com.rentmycar.services.AccelerationService
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.http.*

class AccelerationController {
    private val accelerationService = AccelerationService()

    suspend fun calculateAcceleration(call: ApplicationCall) {
        val ax = call.parameters["ax"]?.toDoubleOrNull() ?: return call.respond(HttpStatusCode.BadRequest, "Invalid ax")
        val ay = call.parameters["ay"]?.toDoubleOrNull() ?: return call.respond(HttpStatusCode.BadRequest, "Invalid ay")
        val az = call.parameters["az"]?.toDoubleOrNull() ?: return call.respond(HttpStatusCode.BadRequest, "Invalid az")

        val magnitude = accelerationService.calculateAccelerationMagnitude(ax, ay, az)
        call.respond(HttpStatusCode.OK, "Acceleration Magnitude: $magnitude")
    }

    suspend fun calculateVelocity(call: ApplicationCall) {
        val initialVelocity = call.parameters["initialVelocity"]?.toDoubleOrNull() ?: return call.respond(HttpStatusCode.BadRequest, "Invalid initialVelocity")
        val acceleration = call.parameters["acceleration"]?.toDoubleOrNull() ?: return call.respond(HttpStatusCode.BadRequest, "Invalid acceleration")
        val deltaTime = call.parameters["deltaTime"]?.toDoubleOrNull() ?: return call.respond(HttpStatusCode.BadRequest, "Invalid deltaTime")

        val velocity = accelerationService.calculateVelocity(initialVelocity, acceleration, deltaTime)
        call.respond(HttpStatusCode.OK, "Velocity: $velocity")
    }
}
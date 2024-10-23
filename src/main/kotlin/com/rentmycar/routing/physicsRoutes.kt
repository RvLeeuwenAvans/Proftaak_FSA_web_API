package com.rentmycar.routing

import com.rentmycar.controllers.TimeSlotController

import io.ktor.server.routing.*

fun Route.physicsRoutes() {
    val timeSlotController = TimeSlotController()

    route("/physics") {
        get("/acceleration") { timeSlotController.calculateAcceleration(call) }
        get("/velocity") { timeSlotController.calculateVelocity(call) }
    }
}
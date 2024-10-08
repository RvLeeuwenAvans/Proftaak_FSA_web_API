package com.rentmycar.routing

import com.rentmycar.controllers.TimeSlotController
import io.ktor.server.auth.*
import io.ktor.server.routing.*

fun Route.timeSlotRoutes() {
    val reservationController = TimeSlotController()

    authenticate {
        route("/timeSlot") {
            post("/create") { reservationController.createTimeslot(call) }
        }
    }
}
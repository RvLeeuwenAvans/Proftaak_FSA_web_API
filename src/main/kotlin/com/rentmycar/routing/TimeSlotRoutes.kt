package com.rentmycar.routing

import com.rentmycar.modules.availability.TimeSlotController
import io.ktor.server.auth.*
import io.ktor.server.routing.*

fun Route.timeSlotRoutes() {
    val reservationController = TimeSlotController()

    authenticate {
        post("/timeSlot/create") { reservationController.createTimeslot(call) }
    }
}
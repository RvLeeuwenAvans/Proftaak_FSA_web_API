package com.rentmycar.routing

import com.rentmycar.controllers.TimeSlotController
import io.ktor.server.auth.*
import io.ktor.server.routing.*

fun Route.timeSlotRoutes() {
    val timeSlotController = TimeSlotController()

    authenticate {
        route("/timeSlot") {
            post("/create") { timeSlotController.createTimeSlot(call) }
//            get { "/{id}" } { timeSlotController.readTimeSlot(call) }
            post("/update") { timeSlotController.updateTimeSlot(call) }
            delete("/remove/{id}") { timeSlotController.removeTimeSlot(call) }
        }
    }
}
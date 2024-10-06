package com.rentmycar.routing

import com.rentmycar.controllers.TimeSlotController
import io.ktor.server.auth.*
import io.ktor.server.routing.*

fun Route.timeSlotRoutes() {
    val timeSlotController = TimeSlotController()

    authenticate {
        post("/timeSlot/create") { timeSlotController.createTimeSlot(call) }
        get { "/timeSlot/{id}" } { timeSlotController.readTimeSlot(call) }
        post("/timeSlot/update") { timeSlotController.updateTimeSlot(call) }
        delete("/timeSlot/remove/{id}") { timeSlotController.removeTimeSlot(call) }
    }
}
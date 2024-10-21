package com.rentmycar.routing

import com.rentmycar.routing.controllers.TimeSlotController
import io.ktor.server.auth.*
import io.ktor.server.routing.*

fun Route.timeSlotRoutes() {
    val timeSlotController = TimeSlotController()

    authenticate {
        route("/timeSlots") {
            get("/{id}") { timeSlotController.getTimeslotById(call) }
            get("/car/{carId}}") { timeSlotController.getTimeslotsByCarId(call) }
            get("/between/{fromDate}/{untilDate}") { timeSlotController.getTimeslotsByDateRange(call) }
            post("/create") { timeSlotController.createTimeSlot(call) }
            patch("/update") { timeSlotController.updateTimeSlot(call) }
            delete("/{id}") { timeSlotController.removeTimeSlot(call) }
        }
    }
}
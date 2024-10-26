package com.rentmycar.routing

import com.rentmycar.controllers.TimeSlotController
import io.ktor.server.auth.*
import io.ktor.server.routing.*

fun Route.timeSlotRoutes() {
    val timeSlotController = TimeSlotController()

    authenticate {
        route("/timeSlot") {
            get("/{id}") { timeSlotController.getTimeslotById(call) }
            get("/car/{id}") { timeSlotController.getTimeslotsByCarId(call) }
            get("/all/between/{fromDate}/{untilDate}") { timeSlotController.getTimeslotsByDateRange(call) }
            post("/create") { timeSlotController.createTimeSlot(call) }
            patch("/update") { timeSlotController.updateTimeSlot(call) }
            delete("/{id}") { timeSlotController.removeTimeSlot(call) }
        }
    }
}
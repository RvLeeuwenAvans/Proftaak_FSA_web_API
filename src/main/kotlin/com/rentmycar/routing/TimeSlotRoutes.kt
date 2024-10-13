package com.rentmycar.routing

import com.rentmycar.controllers.TimeSlotController
import io.ktor.server.auth.*
import io.ktor.server.routing.*

fun Route.timeSlotRoutes() {
    val timeSlotController = TimeSlotController()

    authenticate {
        route("/timeSlot") {
            post("/create") { timeSlotController.createTimeSlot(call) }
            get("/{timeslotId}}") { timeSlotController.getTimeslotById(call) }
            patch("/update") { timeSlotController.updateTimeSlot(call) }
            delete("/{timeslotId}}") { timeSlotController.removeTimeSlot(call) }
            route("/all") {
                get("/car/{carId}}") { timeSlotController.getTimeslotsByCarId(call) }
                get("/from/{fromDate}/until/{untilDate}") { timeSlotController.getTimeslotsByDateRange(call) }
            }
        }
    }
}
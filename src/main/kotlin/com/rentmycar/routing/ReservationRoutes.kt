package com.rentmycar.routing

import com.rentmycar.controllers.ReservationController
import io.ktor.server.auth.*
import io.ktor.server.routing.*

fun Route.reservationRoutes() {
    val reservationController = ReservationController()

    authenticate {
        route("/reservations") {
            post("/create") { reservationController.createReservation(call) }
            get("/user") { reservationController.getReservationsForUser(call) }
            get("/timeslot/{id}") { reservationController.getReservationForTimeSlot(call) }
            delete("/{id}") { reservationController.removeReservation(call) }
        }
    }
}
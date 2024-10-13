package com.rentmycar.routing

import com.rentmycar.controllers.ReservationController
import io.ktor.server.auth.*
import io.ktor.server.routing.*

fun Route.reservationRoutes() {
    val reservationController = ReservationController()

    authenticate {
        route("/reservation") {
            post("/create") { reservationController.createReservation(call) }
            post("/remove") { reservationController.removeReservation(call) }
        }
    }
}
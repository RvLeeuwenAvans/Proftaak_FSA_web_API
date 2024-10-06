package com.rentmycar.routing

import com.rentmycar.modules.users.reservations.ReservationService
import io.ktor.server.auth.*
import io.ktor.server.routing.*

fun Route.reservationRoutes() {
    val reservationController = ReservationService()

    authenticate {
        post("/reservation/create") { reservationController.createReservation(call) }
        post("/reservation/remove") { reservationController.removeReservation(call) }
    }
}
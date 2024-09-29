package com.rentmycar.routing

import com.rentmycar.controllers.ReservationController
import io.ktor.server.routing.*

fun Route.reservationRoutes() {
    val reservationController = ReservationController()

//    authenticate {
//        post("/reservation/create") { reservationController.register(call) }
//    }
}
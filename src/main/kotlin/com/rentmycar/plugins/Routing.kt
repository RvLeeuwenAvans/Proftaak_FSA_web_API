package com.rentmycar.plugins

import com.rentmycar.routing.userRoutes
import com.rentmycar.routing.carRoutes
import com.rentmycar.routing.reservationRoutes
import com.rentmycar.routing.timeSlotRoutes
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        userRoutes()
        carRoutes()
        timeSlotRoutes()
        reservationRoutes()
    }
}

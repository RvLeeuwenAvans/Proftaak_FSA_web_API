package com.rentmycar.plugins

import com.rentmycar.routing.*
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        userRoutes()
        carRoutes()
        fuelRoutes()
        brandRoutes()
        modelRoutes()
        timeSlotRoutes()
        reservationRoutes()
        imageRoutes()
    }
}

package com.rentmycar.routing

import com.rentmycar.controllers.AccelerationController
import io.ktor.server.auth.*
import io.ktor.server.routing.*

fun Route.accelerationRoutes() {
    val accelerationController = AccelerationController()

    authenticate {
        route("/acceleration") {
            get("/") { accelerationController.provideAccelerationData(call) }
            get("/velocity") { accelerationController.calculateVelocity(call) }
        }
    }
}
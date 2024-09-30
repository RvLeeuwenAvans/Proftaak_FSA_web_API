package com.rentmycar.routing

import com.rentmycar.controllers.CarController
import io.ktor.server.auth.*
import io.ktor.server.routing.*

fun Route.carRoutes() {
    val carController = CarController()

    authenticate {
        post("/car/register") { carController.registerCar(call) }
    }
}
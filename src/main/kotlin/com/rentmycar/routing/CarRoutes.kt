package com.rentmycar.routing

import com.rentmycar.authentication.jwtConfig
import com.rentmycar.controllers.CarController
import io.ktor.server.auth.*
import io.ktor.server.routing.*

fun Route.carRoutes() {
    val carController = CarController(jwtConfig(environment.config))

    authenticate {
        get("/car/register") { carController.register(call) }
    }
}
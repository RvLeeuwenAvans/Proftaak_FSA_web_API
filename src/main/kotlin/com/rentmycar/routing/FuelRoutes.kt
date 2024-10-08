package com.rentmycar.routing

import com.rentmycar.controllers.FuelController
import io.ktor.server.auth.*
import io.ktor.server.routing.*

fun Route.fuelRoutes() {
    val fuelController = FuelController()

    authenticate {
        route("/fuel") {
            get("/all") { fuelController.getAllFuels(call) }
        }
    }
}
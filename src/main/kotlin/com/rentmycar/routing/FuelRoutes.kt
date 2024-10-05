package com.rentmycar.routing

import com.rentmycar.controllers.FuelController
import io.ktor.server.auth.*
import io.ktor.server.routing.*

fun Route.fuelRoutes() {
    val fuelController = FuelController()
    val prefix = "/fuel"

    authenticate {
        get("$prefix/all") { fuelController.getAllFuels(call) }
    }
}
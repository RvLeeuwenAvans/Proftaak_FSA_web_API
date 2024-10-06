package com.rentmycar.routing

import com.rentmycar.modules.cars.fuels.FuelService
import io.ktor.server.auth.*
import io.ktor.server.routing.*

fun Route.fuelRoutes() {
    val fuelController = FuelService()
    val prefix = "/fuel"

    authenticate {
        get("$prefix/all") { fuelController.getAllFuels(call) }
    }
}
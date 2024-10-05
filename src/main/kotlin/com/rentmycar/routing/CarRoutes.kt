package com.rentmycar.routing

import com.rentmycar.controllers.CarController
import io.ktor.server.auth.*
import io.ktor.server.routing.*

fun Route.carRoutes() {

    val carController = CarController()
    val prefix = "/car"

    authenticate {
        post("$prefix/register") { carController.registerCar(call) }
        put("$prefix/update") { carController.updateCar(call) }
        get("$prefix/filtered") { carController.getFilteredCars(call) }
        delete("$prefix/delete/{id}") { carController.deleteCar(call) }
    }
}
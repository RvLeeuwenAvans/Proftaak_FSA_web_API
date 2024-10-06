package com.rentmycar.routing

import com.rentmycar.modules.cars.CarService
import io.ktor.server.auth.*
import io.ktor.server.routing.*

fun Route.carRoutes() {

    val carController = CarService()
    val prefix = "/car"

    authenticate {
        post("$prefix/register") { carController.registerCar(call) }
        put("$prefix/update") { carController.updateCar(call) }
        get("$prefix/filtered") { carController.getFilteredCars(call) }
        delete("$prefix/delete/{id}") { carController.deleteCar(call) }
    }
}
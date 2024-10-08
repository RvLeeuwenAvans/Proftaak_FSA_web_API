package com.rentmycar.routing

import com.rentmycar.controllers.CarController
import io.ktor.server.auth.*
import io.ktor.server.routing.*

fun Route.carRoutes() {

    val carController = CarController()

    authenticate {
        route("/car") {
            post("/register") { carController.registerCar(call) }
            put("/update") { carController.updateCar(call) }
            get("/filtered") { carController.getFilteredCars(call) }
            delete("/delete/{id}") { carController.deleteCar(call) }
        }
    }
}
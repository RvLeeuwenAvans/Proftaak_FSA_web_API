package com.rentmycar.routing

import com.rentmycar.routing.controllers.CarController
import io.ktor.server.auth.*
import io.ktor.server.routing.*

fun Route.carRoutes() {

    val carController = CarController()

    authenticate {
        route("/car") {
            post("/register") { carController.registerCar(call) }
            put("/update") { carController.updateCar(call) }
            get("/filtered") { carController.getFilteredCars(call) }
            get("/directions") {carController.getDirectionsToCar(call) }
            delete("/delete/{id}") { carController.deleteCar(call) }
        }
    }
}
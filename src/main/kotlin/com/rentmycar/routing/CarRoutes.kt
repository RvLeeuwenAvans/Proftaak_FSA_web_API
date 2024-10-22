package com.rentmycar.routing

import com.rentmycar.controllers.CarController
import io.ktor.server.auth.*
import io.ktor.server.routing.*

fun Route.carRoutes() {

    val carController = CarController()

    authenticate {
        route("/car") {
            post("/register") { carController.registerCar(call) }
            get("/{id}") { carController.getFilteredCars(call) }
            get("/all/filtered") { carController.getFilteredCars(call) }
            get("/directions") { carController.getDirectionsToCar(call) }
            put("/update") { carController.updateCar(call) }
            delete("/{id}") { carController.deleteCar(call) }

            route("/{id}/") {
                get("cost/annual") { carController.getTotalCostOfOwnership(call) }
                get("cost/kilometer/{kilometers}") { carController.getPricePerKilometer(call) }
            }
        }
    }
}
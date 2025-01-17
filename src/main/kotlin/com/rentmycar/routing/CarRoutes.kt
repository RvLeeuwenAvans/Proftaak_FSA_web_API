package com.rentmycar.routing

import com.rentmycar.controllers.CarController
import com.rentmycar.controllers.LocationController
import io.ktor.server.auth.*
import io.ktor.server.config.*
import io.ktor.server.routing.*

fun Route.carRoutes(config: ApplicationConfig) {

    val carController = CarController(config)
    val locationController = LocationController()

    authenticate {
        route("/car") {
            post("/register") { carController.registerCar(call) }
            get("/{id}") { carController.getCar(call) }
            get("/all/filtered") { carController.getFilteredCars(call) }

            post("/directions") { carController.getDirectionsToCar(call) }
            put("/update") { carController.updateCar(call) }
            delete("/{id}") { carController.deleteCar(call) }

            get("/{id}/location") { locationController.getLocation(call) }
            post("/location") { locationController.addLocation(call) }
            put("/location") { locationController.updateLocation(call) }

            route("/{id}/") {
                get("cost/annual") { carController.getTotalCostOfOwnership(call) }
                get("cost/kilometer/{kilometers}") { carController.getPricePerKilometer(call) }
            }
        }
    }
}
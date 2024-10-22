package com.rentmycar.routing

import com.rentmycar.controllers.CarController
import com.rentmycar.controllers.LocationController
import io.ktor.server.auth.*
import io.ktor.server.routing.*

fun Route.carRoutes() {

    val carController = CarController()
    val locationController = LocationController()

    authenticate {
        route("/car") {
            post("/register") { carController.registerCar(call) }
            get("/{id}") { carController.getCar(call) }
            get("/all/filtered") { carController.getFilteredCars(call) }
            get("/location") { locationController.getLocation(call) }
            get("/directions") { carController.getDirectionsToCar(call) }
            put("/update") { carController.updateCar(call) }
            delete("/{id}") { carController.deleteCar(call) }

            route("/location/") {
                post { locationController.addLocation(call) }
                put { locationController.updateLocation(call) }
            }

            route("/{id}/") {
                get("cost/annual") { carController.getTotalCostOfOwnership(call) }
                get("cost/kilometer/{kilometers}") { carController.getPricePerKilometer(call) }
            }
        }
    }
}
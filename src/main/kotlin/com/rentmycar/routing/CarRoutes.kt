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
            put("/update") { carController.updateCar(call) }
            get("/filtered") { carController.getFilteredCars(call) }
            get("/directions") {carController.getDirectionsToCar(call) }
            delete("/delete/{id}") { carController.deleteCar(call) }

            route("/location") {
                post("/") { locationController.addLocation(call) }
                put("/") { locationController.updateLocation(call) }
            }
        }
    }
}
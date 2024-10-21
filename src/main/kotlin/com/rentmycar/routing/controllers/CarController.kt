package com.rentmycar.routing.controllers

import com.rentmycar.entities.toDTO
import com.rentmycar.plugins.user
import com.rentmycar.repositories.CarRepository
import com.rentmycar.repositories.LocationRepository
import com.rentmycar.routing.controllers.requests.car.DirectionsToCarRequest
import com.rentmycar.routing.controllers.requests.car.RegisterCarRequest
import com.rentmycar.routing.controllers.requests.car.UpdateCarRequest
import com.rentmycar.services.CarService
import com.rentmycar.services.ModelService
import com.rentmycar.utils.LocationData
import com.rentmycar.utils.sanitizeId
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*


class CarController {
    private val carService = CarService()
    private val modelService = ModelService()

    suspend fun registerCar(call: ApplicationCall) {
        val user = call.user()

        val registrationRequest = call.receive<RegisterCarRequest>()
        registrationRequest.validate()

        val model = modelService.get(registrationRequest.modelId)
        carService.register(user, model, registrationRequest)

        call.respond(HttpStatusCode.OK, "Car registered successfully")
    }


    suspend fun updateCar(call: ApplicationCall) {
        val user = call.user()
        val updateRequest = call.receive<UpdateCarRequest>()

        updateRequest.validate()
        carService.update(user, updateRequest)

        call.respond(HttpStatusCode.OK, "Car updated successfully")
    }

    suspend fun getFilteredCars(call: ApplicationCall) {
        val ownerId = sanitizeId(call.request.queryParameters["ownerId"])
        val category = call.request.queryParameters["category"]
        val minPrice = call.request.queryParameters["minPrice"]?.toIntOrNull()
        val maxPrice = call.request.queryParameters["maxPrice"]?.toIntOrNull()

        val longitude = call.request.queryParameters["longitude"]?.toDoubleOrNull()
        val latitude = call.request.queryParameters["latitude"]?.toDoubleOrNull()
        var radius = call.request.queryParameters["radius"]?.toIntOrNull()

        // If radius is provided and not null, we can filter the cars by radius only if
        // coordinates (longitude and latitude) of the user are provided and valid.
        // Therefore:
        if (
            longitude == null ||
            latitude == null ||
            longitude !in -90.0..90.0 ||
            latitude !in -90.0..90.0
        ) {
            radius = null
        }

        val filteredCars = CarRepository().getFilteredCars(
            ownerId = if (ownerId != -1) ownerId else null,
            category = category,
            minPrice = minPrice,
            maxPrice = maxPrice,
            locationData = if (radius != null) LocationData(
                latitude = latitude!!,
                longitude = longitude!!,
                radius = radius
            ) else null,
        )

        return call.respond(
            HttpStatusCode.OK,
            filteredCars.map { it.toDTO() }
        )
    }

    suspend fun getDirectionsToCar(call: ApplicationCall) {
        val request = call.receive<DirectionsToCarRequest>()
        request.validate()

        val location = LocationRepository().getByCar(request.carId)
        return call.respond(
            "https://www.google.com/maps/dir/?api=1&origin=${request.latitude},${request.longitude}&destination=${location.latitude},${location.longitude}&travelmode=walking"
        )
    }

    suspend fun deleteCar(call: ApplicationCall) {
        val user = call.user()
        val carId = sanitizeId(call.parameters["id"])

        carService.delete(user, carId)

        return call.respond(
            HttpStatusCode.OK,
            "Car deleted successfully."
        )
    }
}

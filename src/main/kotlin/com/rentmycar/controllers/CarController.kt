package com.rentmycar.controllers

import com.rentmycar.entities.toDTO
import com.rentmycar.plugins.user
import com.rentmycar.repositories.CarRepository
import com.rentmycar.repositories.LocationRepository
import com.rentmycar.requests.car.DirectionsToCarRequest
import com.rentmycar.requests.car.RegisterCarRequest
import com.rentmycar.requests.car.UpdateCarRequest
import com.rentmycar.services.CarService
import com.rentmycar.services.ModelService
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

    // TODO: part of the epic link: https://proftaakfsa1.atlassian.net/browse/kan-28

    suspend fun getFilteredCars(call: ApplicationCall) {
        val ownerId = sanitizeId(call.request.queryParameters["ownerId"])

        if (ownerId == -1) return call.respond(
            HttpStatusCode.BadRequest,
            "Owner ID is invalid."
        )

        val filteredCars = CarRepository().getFilteredCars(
            ownerId,
            // etc.
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

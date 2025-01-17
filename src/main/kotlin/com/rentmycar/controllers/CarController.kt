package com.rentmycar.controllers

import com.rentmycar.dtos.requests.car.DirectionsToCarRequest
import com.rentmycar.dtos.requests.car.RegisterCarRequest
import com.rentmycar.dtos.requests.car.UpdateCarRequest
import com.rentmycar.entities.toDTO
import com.rentmycar.plugins.user
import com.rentmycar.repositories.LocationRepository
import com.rentmycar.services.CarService
import com.rentmycar.services.ModelService
import com.rentmycar.utils.sanitizeId
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.config.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*


class CarController(val config: ApplicationConfig) {
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

        CarService.ensureUserIsCarOwner(user, updateRequest.carId)
        val carBusinessObject = CarService.getBusinessObject(updateRequest.carId)
        carBusinessObject.update(updateRequest)

        call.respond(HttpStatusCode.OK, "Car updated successfully")
    }

    suspend fun getCar(call: RoutingCall) {
        val carId = sanitizeId(call.parameters["id"])

        val carBusinessObject = CarService.getBusinessObject(carId).getCar()

        call.respond(HttpStatusCode.OK, carBusinessObject.toDTO())
    }

    suspend fun getTotalCostOfOwnership(call: RoutingCall) {
        val carId = sanitizeId(call.parameters["id"])
        val user = call.user()

        CarService.ensureUserIsCarOwner(user, carId)
        val carBusinessObject = CarService.getBusinessObject(carId)
        call.respond(HttpStatusCode.OK, carBusinessObject.calculateTotalOwnershipCosts())
    }

    suspend fun getPricePerKilometer(call: RoutingCall) {
        val carId = sanitizeId(call.parameters["id"])
        val kilometers = sanitizeId(call.parameters["kilometers"])
        val user = call.user()

        CarService.ensureUserIsCarOwner(user, carId)
        val carBusinessObject = CarService.getBusinessObject(carId)
        call.respond(HttpStatusCode.OK, carBusinessObject.calculatePricePerKilometer(kilometers))
    }

    suspend fun getFilteredCars(call: ApplicationCall) {
        val ownerId = sanitizeId(call.request.queryParameters["ownerId"])
        val category = call.request.queryParameters["category"]
        val minPrice = call.request.queryParameters["minPrice"]?.toIntOrNull()
        val maxPrice = call.request.queryParameters["maxPrice"]?.toIntOrNull()

        val longitude = call.request.queryParameters["longitude"]?.toDoubleOrNull()
        val latitude = call.request.queryParameters["latitude"]?.toDoubleOrNull()
        val radius = call.request.queryParameters["radius"]?.toIntOrNull()

        val filteredCars = carService.getCars(longitude, latitude, radius, ownerId, category, minPrice, maxPrice)

        return call.respond(
            HttpStatusCode.OK,
            filteredCars.map { it.toDTO() }
        )
    }

    suspend fun getDirectionsToCar(call: ApplicationCall) {
        val request = call.receive<DirectionsToCarRequest>()
        request.validate()

        val location = LocationRepository().getByCar(request.carId)
        val apiKey = config.property("ktor.api_key_conf.google_maps_api_key").getString()

        val response = HttpClient().get(
            "https://maps.googleapis.com/maps/api/directions/json?api=1&origin=" +
                    "${request.latitude},${request.longitude}&destination=${location.latitude},${location.longitude}" +
                    "&travelmode=walking&key=${apiKey}"
        ).bodyAsText()

        call.respond(response)
    }

    suspend fun deleteCar(call: ApplicationCall) {
        val user = call.user()
        val carId = sanitizeId(call.parameters["id"])
        CarService.ensureUserIsCarOwner(user, carId)
        CarService.getBusinessObject(carId).delete()

        return call.respond(
            HttpStatusCode.OK,
            "Car deleted successfully."
        )
    }
}

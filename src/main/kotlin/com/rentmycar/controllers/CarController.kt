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
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import javax.naming.AuthenticationException
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.server.response.respond

class CarController {
    private val carService = CarService()
    private val modelService = ModelService()

suspend fun getOwnerCars(call: ApplicationCall) {
    try {
        val user = call.user() // This uses the extension function we saw in Security.kt
        val cars = carService.getCarsByOwnerId(user.id.value)
        call.respond(HttpStatusCode.OK, cars)
    } catch (e: AuthenticationException) {
        call.respond(HttpStatusCode.Unauthorized, "Invalid or missing token")
    } catch (e: Exception) {
        call.respond(HttpStatusCode.InternalServerError, "An error occurred while fetching cars")
    }
}

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
        return call.respond(
            "https://www.google.com/maps/dir/?api=1&origin=${request.latitude},${request.longitude}&destination=${location.latitude},${location.longitude}&travelmode=walking"
        )
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

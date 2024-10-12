package com.rentmycar.controllers

import com.rentmycar.entities.Car
import com.rentmycar.entities.toDTO
import com.rentmycar.plugins.user
import com.rentmycar.repositories.CarRepository
import com.rentmycar.repositories.ModelRepository
import com.rentmycar.repositories.TimeSlotRepository
import com.rentmycar.requests.car.RegisterCarRequest
import com.rentmycar.requests.car.UpdateCarRequest
import com.rentmycar.utils.sanitizeId
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*


class CarController {

    private val carRepository = CarRepository()
    private val modelRepository = ModelRepository()  // Assuming you have a ModelRepository
    private val timeslotRepository = TimeSlotRepository()

    /**
     * Check whether the user is the owner of the car.
     */
    private fun isCarOwner(carId: Int, userId: Int) {
        val foundCar = carRepository.getCarById(carId)

        if (foundCar == null) throw Error("Car not found.")
        if (foundCar.ownerId.value != userId)
            throw Error("Only owner of the car can update the car.")
    }
    private fun isCarOwner(car: Car, userId: Int) {
        if (car.ownerId.value != userId) throw error("Only owner of the car can update the car.")
    }

    // Register a new car
    suspend fun registerCar(call: ApplicationCall) {
        val user = call.user()

        val registrationRequest = call.receive<RegisterCarRequest>()
        val validationErrors = registrationRequest.validate()

        // Validate the request data
        if (validationErrors.isNotEmpty()) return call.respond(
            HttpStatusCode.BadRequest,
            "Invalid registration data: ${validationErrors.joinToString(", ")}"
        )

        // Check if the license plate already exists
        if (carRepository.doesLicensePlateExist(registrationRequest.licensePlate)) return call.respond(
            HttpStatusCode.Conflict,
            "License plate is already registered"
        )

        // Fetch model and fuel by their ids (assuming repositories exist)
        val model = modelRepository.getModel(registrationRequest.modelId)
            ?: return call.respond(HttpStatusCode.NotFound, "Model not found")

        // register the car
        carRepository.registerCar(
            owner = user,
            licensePlate = registrationRequest.licensePlate,
            model = model,
            fuel = registrationRequest.fuel,
            year = registrationRequest.year,
            color = registrationRequest.color,
            transmission = registrationRequest.transmission,
            price = registrationRequest.price,
        )

        call.respond(HttpStatusCode.OK, "Car registered successfully")
    }

    /**
     * Update the car.
     */
    suspend fun updateCar(call: ApplicationCall) {
        val user = call.user()
        val request = call.receive<UpdateCarRequest>()
        val validationErrors = request.validate()

        // Validate request.
        if (validationErrors.isNotEmpty()) return call.respond(
            HttpStatusCode.BadRequest,
            "invalid registration data: ${validationErrors.joinToString(", ")}."
        )

        // Make sure the user is the owner of the car.
        try {
            isCarOwner(request.carId, user.id.value)
        } catch (e: Error) {
            return call.respond(
                HttpStatusCode.BadRequest,
                e.message.toString()
            )
        }

        // Update the car.
        carRepository.updateCar(
            id = request.carId,
            year = request.year,
            color = request.color,
            transmission = request.transmission,
            fuel = request.fuel,
            price = request.price,
        )

        call.respond(HttpStatusCode.OK, "Car updated successfully")
    }

    // TODO: part of the epic link: https://proftaakfsa1.atlassian.net/browse/kan-28
    /**
     * Get filtered cars based on some criteria.
     */
    suspend fun getFilteredCars(call: ApplicationCall) {
        var ownerId = sanitizeId(call.request.queryParameters["ownerId"])

        if (ownerId == -1) return call.respond(
            HttpStatusCode.BadRequest,
            "Owner ID is invalid."
        )

        val filteredCars = carRepository.getFilteredCars(
            ownerId,
            // etc.
        )

        return call.respond(
            HttpStatusCode.OK,
            filteredCars.map { it.toDTO() }
        )
    }

    /**
     * Delete the car.
     */
    suspend fun deleteCar(call: ApplicationCall) {
        val user = call.user()
        val carId = sanitizeId(call.parameters["id"])

        // Validate the request.
        if (carId == -1) return call.respond(
            HttpStatusCode.BadRequest,
            "Car ID is invalid."
        )

        // Make sure car exists.
        val foundCar = carRepository.getCarById(carId)
        if (foundCar == null) return call.respond(HttpStatusCode.NotFound, "Car not found."
        )

        // Make sure user is the owner of the car.
        try {
            isCarOwner(foundCar, user.id.value)
        } catch (e: Error) {
            return call.respond(
                HttpStatusCode.BadRequest,
                e.message.toString()
            )
        }

        // Make sure there are no timeslots linked to the car.
        if (timeslotRepository.hasLinkedTimeslots(foundCar)) return call.respond(
            HttpStatusCode.BadRequest,
            "Car cannot be deleted while it has linked timeslots."
        )

        carRepository.deleteCar(carId)
        return call.respond(
            HttpStatusCode.OK,
            "Car deleted successfully."
        )
    }
}

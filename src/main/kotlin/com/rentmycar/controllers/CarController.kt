package com.rentmycar.controllers

import com.rentmycar.plugins.user
import com.rentmycar.repositories.CarRepository
import com.rentmycar.requests.car.RegisterCarRequest
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*

class CarController {

    private val carRepository = CarRepository()

    suspend fun registerCar(call: ApplicationCall) {
        val user  = call.user()

        val registrationRequest = call.receive<RegisterCarRequest>()
        val validationErrors = registrationRequest.validate()

        if (validationErrors.isNotEmpty()) return call.respond(
            HttpStatusCode.BadRequest,
            "Invalid registration data: ${validationErrors.joinToString(", ")}"
        )

        if (carRepository.doesLicensePlateExist(registrationRequest.licensePlate)) return call.respond(
            HttpStatusCode.Conflict,
            "License plate is already registered"
        )

        carRepository.registerCar(user, registrationRequest.licensePlate)
        call.respond(HttpStatusCode.OK, "Car registered successfully")
    }
}
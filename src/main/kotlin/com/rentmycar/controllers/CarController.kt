package com.rentmycar.controllers

import com.rentmycar.repositories.CarRepository
import com.rentmycar.repositories.UserRepository
import com.rentmycar.requests.RegisterCarRequest
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*

class CarController {

    private val carRepository = CarRepository()
    private val userRepository = UserRepository()

    suspend fun register(call: ApplicationCall) {

        val registrationRequest = call.receive<RegisterCarRequest>()
        val validationErrors = registrationRequest.validate()

        if (validationErrors.isNotEmpty()) {
            call.respond(HttpStatusCode.BadRequest, "Invalid registration data: ${validationErrors.joinToString(", ")}")
            return
        }

        val user = userRepository.getUserById(registrationRequest.userId)

        if (user == null) {
            call.respond(HttpStatusCode.NotFound, "User does not exist")
            return
        }

        if (carRepository.doesLicensePlateExist(registrationRequest.licensePlate)) {
            call.respond(HttpStatusCode.Conflict, "License plate is already registered")
            return
        }

        carRepository.registerCar(user, registrationRequest.licensePlate)

        call.respond(HttpStatusCode.OK, "Car registered successfully")
    }
}
package com.rentmycar.controllers

import com.rentmycar.entities.User
import com.rentmycar.repositories.CarRepository
import com.rentmycar.repositories.UserRepository
import com.rentmycar.requests.RegisterCarRequest
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*

class CarController {

    private val carRepository = CarRepository()

    suspend fun register(call: ApplicationCall) {
        val principal = call.principal<JWTPrincipal>()

        // todo: refactor
        val userId = principal?.payload?.getClaim("id")?.asInt()

        if (userId == null) {
            call.respond(HttpStatusCode.NotFound, "User doesn't exist")
            return
        }

        // todo: refactor
        val user = UserRepository().getUserById(userId)

        val registrationRequest = call.receive<RegisterCarRequest>()
        val validationErrors = registrationRequest.validate()

        if (validationErrors.isNotEmpty()) {
            call.respond(HttpStatusCode.BadRequest, "Invalid registration data: ${validationErrors.joinToString(", ")}")
            return
        }

        if (carRepository.doesLicensePlateExist(registrationRequest.licensePlate)) {
            call.respond(HttpStatusCode.Conflict, "License plate is already registered")
            return
        }

        // todo: refactor
        if (user != null) {
            carRepository.registerCar(user, registrationRequest.licensePlate)
            call.respond(HttpStatusCode.OK, "Car registered successfully")
        }
    }
}
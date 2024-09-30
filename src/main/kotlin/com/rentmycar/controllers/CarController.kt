package com.rentmycar.controllers

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

    suspend fun registerCar(call: ApplicationCall) {
        val principal = call.principal<JWTPrincipal>()
        val userId = principal?.payload?.getClaim("id")?.asInt()

        val user = userId?.let { UserRepository().getUserById(it) } ?: return call.respond(
            HttpStatusCode.NotFound,
            "User not found"
        )

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
package com.rentmycar.controllers

import com.rentmycar.repositories.CarRepository
import com.rentmycar.repositories.TimeSlotRepository
import com.rentmycar.requests.CreateTimeSlotRequest
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import kotlinx.datetime.toJavaLocalDateTime

class TimeSlotController {

    private val timeSlotRepository = TimeSlotRepository()

    suspend fun createTimeslot(call: ApplicationCall) {
        val principal = call.principal<JWTPrincipal>()
        val userId = principal?.payload?.getClaim("id")?.asInt()

        val createTimeSlotRequest = call.receive<CreateTimeSlotRequest>()
        val validationErrors = createTimeSlotRequest.validate()

        if (validationErrors.isNotEmpty()) return call.respond(
            HttpStatusCode.BadRequest,
            "Invalid creation data: ${validationErrors.joinToString(", ")}"
        )

        val car = CarRepository().getCarById(createTimeSlotRequest.carId)
            ?: return call.respond(HttpStatusCode.NotFound, "Car does not exist")

        if (userId != car.ownerId.value) return call.respond(HttpStatusCode.BadRequest, "user is not the car's owner")

        val availableFrom = createTimeSlotRequest.availableFrom.toJavaLocalDateTime()
        val availableUntil = createTimeSlotRequest.availableUntil.toJavaLocalDateTime()

        if (timeSlotRepository.doesTimeSlotHaveConflicts(car, availableFrom, availableUntil)) return call.respond(
            HttpStatusCode.Conflict,
            "timeslot overlaps with already an existing time-slots."
        )


        timeSlotRepository.createTimeSlot(car, availableFrom, availableUntil)
        call.respond(HttpStatusCode.OK, "Timeslot created successfully")
    }
}
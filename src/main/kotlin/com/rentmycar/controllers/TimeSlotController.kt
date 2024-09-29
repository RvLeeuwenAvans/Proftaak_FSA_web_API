package com.rentmycar.controllers

import com.rentmycar.repositories.CarRepository
import com.rentmycar.repositories.TimeSlotRepository
import com.rentmycar.repositories.UserRepository
import com.rentmycar.requests.CreateTimeSlotRequest
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import kotlinx.datetime.toJavaLocalDateTime

class TimeSlotController {

    private val timeSlotRepository = TimeSlotRepository()

    suspend fun createTimeslot(call: ApplicationCall) {
        val createTimeSlotRequest = call.receive<CreateTimeSlotRequest>()
        val validationErrors = createTimeSlotRequest.validate()

        if (validationErrors.isNotEmpty()) {
            call.respond(HttpStatusCode.BadRequest, "Invalid creation data: ${validationErrors.joinToString(", ")}")
            return
        }

        val car = CarRepository().getCarById(createTimeSlotRequest.carId)

        if (car == null) {
            call.respond(HttpStatusCode.NotFound, "Car does not exist")
            return
        }

        call.respond(HttpStatusCode.NotFound, car.userId)
        return

//        if (car.user.id.get != createTimeSlotRequest.userId) {
//            call.respond(HttpStatusCode.BadRequest, "user is not the car's owner")
//            return
//        }

        val availableFrom = createTimeSlotRequest.availableFrom.toJavaLocalDateTime()
        val availableUntil = createTimeSlotRequest.availableUntil.toJavaLocalDateTime()

        if (timeSlotRepository.doesTimeSlotHaveConflicts(car, availableFrom, availableUntil)) {
            call.respond(
                HttpStatusCode.Conflict,
                "timeslot overlaps with already an existing time-slots."
            )
            return
        }

        timeSlotRepository.createTimeSlot(car, availableFrom, availableUntil)

        call.respond(HttpStatusCode.OK, "Timeslot created successfully")
    }
}
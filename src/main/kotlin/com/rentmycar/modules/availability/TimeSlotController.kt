package com.rentmycar.modules.availability

import com.rentmycar.modules.availability.requests.CreateTimeSlotRequest
import com.rentmycar.plugins.user
import com.rentmycar.modules.cars.CarRepository
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import kotlinx.datetime.toJavaLocalDateTime

class TimeSlotController {

    private val timeSlotRepository = TimeSlotRepository()

    suspend fun createTimeslot(call: ApplicationCall) {
        val user  = call.user()

        val createTimeSlotRequest = call.receive<CreateTimeSlotRequest>()
        val validationErrors = createTimeSlotRequest.validate()

        if (validationErrors.isNotEmpty()) return call.respond(
            HttpStatusCode.BadRequest,
            "Invalid creation data: ${validationErrors.joinToString(", ")}"
        )

        val car = CarRepository().getCarById(createTimeSlotRequest.carId)
            ?: return call.respond(HttpStatusCode.NotFound, "Car does not exist")

        if (user.id.value != car.ownerId.value) {
            return call.respond(HttpStatusCode.BadRequest, "user is not the car's owner")
        }

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
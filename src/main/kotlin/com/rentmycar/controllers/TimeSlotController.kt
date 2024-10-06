package com.rentmycar.controllers

import com.rentmycar.plugins.user
import com.rentmycar.repositories.CarRepository
import com.rentmycar.repositories.TimeSlotRepository
import com.rentmycar.requests.timeslot.CreateTimeSlotRequest
import com.rentmycar.requests.timeslot.TimeSlotUpdateRequest
import com.rentmycar.utils.isNumeric
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import kotlinx.datetime.toJavaLocalDateTime
import java.time.LocalTime

class TimeSlotController {

    private val timeSlotRepository = TimeSlotRepository()

    suspend fun createTimeSlot(call: ApplicationCall) {
        val user = call.user()

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

    suspend fun updateTimeSlot(call: ApplicationCall) {
        val user = call.user()

        val timeSlotUpdateRequest = call.receive<TimeSlotUpdateRequest>()
        val validationErrors = timeSlotUpdateRequest.validate()

        if (validationErrors.isNotEmpty()) return call.respond(
            HttpStatusCode.BadRequest,
            "Invalid update request: ${validationErrors.joinToString(", ")}"
        )

        val timeslot = TimeSlotRepository().getTimeSlot(timeSlotUpdateRequest.timeSlotId)
            ?: return call.respond(HttpStatusCode.NotFound, "Timeslot does not exist")

        if (timeslot.availableFrom.toLocalTime() < LocalTime.now()) return call.respond(
            HttpStatusCode.BadRequest,
            "cannot edit an active or past timeslot"
        )

        if (user.id.value != timeslot.car.ownerId.value) {
            return call.respond(HttpStatusCode.BadRequest, "user is not the timeslot's owner")
        }

        if (timeSlotRepository.doesTimeSlotHaveConflicts(timeslot)) return call.respond(
            HttpStatusCode.Conflict,
            "timeslot overlaps with already an existing timeslots."
        )

        timeSlotRepository.updateTimeSlot(timeslot, timeSlotUpdateRequest)
        call.respond(HttpStatusCode.OK, "Timeslot updated successfully")
    }

    suspend fun removeTimeSlot(call: ApplicationCall) {
        val user = call.user()

        val timeslotId = sanitizeId(call.parameters["id"])

        // Validate the request.
        if (timeslotId == -1) return call.respond(
            HttpStatusCode.BadRequest,
            "Timeslot ID is invalid."
        )

        val timeSlot = timeSlotRepository.getTimeSlot(timeslotId)
            ?: return call.respond(HttpStatusCode.NotFound, "Time slot does not exist")

        if (timeSlot.availableFrom.toLocalTime() < LocalTime.now()) return call.respond(
            HttpStatusCode.BadRequest,
            "cannot delete an active or past timeslot"
        )

        if (user.id.value != timeSlot.car.ownerId.value) {
            return call.respond(HttpStatusCode.BadRequest, "user is not the timeslot's owner")
        }

        timeSlotRepository.deleteTimeSlot(timeSlot)
    }

//    suspend fun getTimeSlot(call: ApplicationCall) {
//        val timeslotId = sanitizeId(call.parameters["id"])
//
//        // Validate the request.
//        if (timeslotId == -1) return call.respond(
//            HttpStatusCode.BadRequest,
//            "Timeslot ID is invalid."
//        )
//
//        val timeSlot = timeSlotRepository.getTimeSlot(timeslotId)
//            ?: return call.respond(HttpStatusCode.NotFound, "Time slot does not exist")
//    }

    private fun sanitizeId(id: String? = null): Int =
        if (id == null || !isNumeric(id)) -1 else id.toInt()
}
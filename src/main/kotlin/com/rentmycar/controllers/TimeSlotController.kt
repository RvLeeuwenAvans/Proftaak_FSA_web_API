package com.rentmycar.controllers

import com.rentmycar.dtos.requests.timeslot.CreateTimeSlotRequest
import com.rentmycar.dtos.requests.timeslot.TimeSlotUpdateRequest
import com.rentmycar.entities.toDTO
import com.rentmycar.services.CarService
import com.rentmycar.services.TimeSlotService
import com.rentmycar.utils.sanitizeId
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.datetime.LocalDateTime
import java.security.InvalidParameterException

class TimeSlotController {
    private val timeSlotService = TimeSlotService()

    suspend fun createTimeSlot(call: ApplicationCall) {
        val createTimeSlotRequest = call.receive<CreateTimeSlotRequest>()
        createTimeSlotRequest.validate()

        val timeSlotRange = createTimeSlotRequest.availableFrom.rangeUntil(createTimeSlotRequest.availableUntil)
        timeSlotService.createTimeSlot(createTimeSlotRequest.carId, timeSlotRange)

        call.respond(HttpStatusCode.OK, "Timeslot created successfully")
    }

    suspend fun updateTimeSlot(call: ApplicationCall) {
        val timeSlotUpdateRequest = call.receive<TimeSlotUpdateRequest>()
        timeSlotUpdateRequest.validate()

        timeSlotService.updateTimeSlot(
            timeSlotUpdateRequest.timeSlotId,
            timeSlotUpdateRequest.availableFrom,
            timeSlotUpdateRequest.availableUntil
        )

        call.respond(HttpStatusCode.OK, "Timeslot updated successfully")
    }

    suspend fun getTimeslotsByDateRange(call: RoutingCall) {
        val from = call.parameters["fromDate"] ?: throw InvalidParameterException("From Date is missing or invalid")
        val until = call.parameters["untilDate"] ?: throw InvalidParameterException("Until Date is missing or invalid")

        val timeSlotRange = LocalDateTime.parse(from).rangeUntil(LocalDateTime.parse(until))
        val timeslots = timeSlotService.getTimeSlots(timeSlotRange)
        call.respond(HttpStatusCode.OK, timeslots.map { it.toDTO() })
    }

    suspend fun getTimeslotById(call: RoutingCall) {
        val timeSlotId = sanitizeId(call.parameters["id"])

        val timeslot = timeSlotService.getTimeSlot(timeSlotId)
        call.respond(HttpStatusCode.OK, timeslot.toDTO())
    }

    suspend fun getTimeslotsByCarId(call: RoutingCall) {
        val carId = sanitizeId(call.parameters["carId"])

        val car = CarService.getBusinessObject(carId)
        val timeslots = timeSlotService.getTimeSlots(car.getCar())

        call.respond(HttpStatusCode.OK, timeslots.map { it.toDTO() })
    }

    suspend fun removeTimeSlot(call: ApplicationCall) {

        val timeslotId = sanitizeId(call.parameters["id"])
        timeSlotService.deleteTimeSlot(timeslotId)

        call.respond(HttpStatusCode.OK, "Timeslot deleted")
    }
}
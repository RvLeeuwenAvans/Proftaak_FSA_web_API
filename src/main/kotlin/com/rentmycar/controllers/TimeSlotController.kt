package com.rentmycar.controllers

import com.rentmycar.entities.toDTO
import com.rentmycar.plugins.user
import com.rentmycar.requests.timeslot.CreateTimeSlotRequest
import com.rentmycar.requests.timeslot.TimeSlotUpdateRequest
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

import com.rentmycar.services.PhysicsService
class TimeSlotController {
    private val timeSlotService = TimeSlotService()
    private val physicsService = PhysicsService()
    suspend fun createTimeSlot(call: ApplicationCall) {
        val user = call.user()

        val createTimeSlotRequest = call.receive<CreateTimeSlotRequest>()
        createTimeSlotRequest.validate()

        val timeSlotRange = createTimeSlotRequest.availableFrom.rangeUntil(createTimeSlotRequest.availableUntil)
        timeSlotService.createTimeSlot(createTimeSlotRequest.carId, user, timeSlotRange)

        call.respond(HttpStatusCode.OK, "Timeslot created successfully")
    }

    suspend fun updateTimeSlot(call: ApplicationCall) {
        val user = call.user()
        val timeSlotUpdateRequest = call.receive<TimeSlotUpdateRequest>()
        timeSlotUpdateRequest.validate()

        timeSlotService.updateTimeSlot(
            timeSlotUpdateRequest.timeSlotId,
            user,
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

        val car = CarService().getCar(carId)
        val timeslots = timeSlotService.getTimeSlots(car)

        call.respond(HttpStatusCode.OK, timeslots.map { it.toDTO() })
    }

    suspend fun removeTimeSlot(call: ApplicationCall) {
        val user = call.user()

        val timeslotId = sanitizeId(call.parameters["id"])
        timeSlotService.deleteTimeSlot(timeslotId, user)

        call.respond(HttpStatusCode.OK, "Timeslot deleted")
    }
        suspend fun calculateAcceleration(call: ApplicationCall) {
        val ax = call.parameters["ax"]?.toDoubleOrNull() ?: return call.respond(HttpStatusCode.BadRequest, "Invalid ax")
        val ay = call.parameters["ay"]?.toDoubleOrNull() ?: return call.respond(HttpStatusCode.BadRequest, "Invalid ay")
        val az = call.parameters["az"]?.toDoubleOrNull() ?: return call.respond(HttpStatusCode.BadRequest, "Invalid az")

        val magnitude = physicsService.calculateAccelerationMagnitude(ax, ay, az)
        call.respond(HttpStatusCode.OK, "Acceleration Magnitude: $magnitude")
    }

    suspend fun calculateVelocity(call: ApplicationCall) {
        val initialVelocity = call.parameters["initialVelocity"]?.toDoubleOrNull() ?: return call.respond(HttpStatusCode.BadRequest, "Invalid initialVelocity")
        val acceleration = call.parameters["acceleration"]?.toDoubleOrNull() ?: return call.respond(HttpStatusCode.BadRequest, "Invalid acceleration")
        val deltaTime = call.parameters["deltaTime"]?.toDoubleOrNull() ?: return call.respond(HttpStatusCode.BadRequest, "Invalid deltaTime")

        val velocity = physicsService.calculateVelocity(initialVelocity, acceleration, deltaTime)
        call.respond(HttpStatusCode.OK, "Velocity: $velocity")
    }
}
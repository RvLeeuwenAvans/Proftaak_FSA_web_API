package com.rentmycar.controllers

import com.rentmycar.entities.toDTO
import com.rentmycar.plugins.user
import com.rentmycar.requests.timeslot.CreateTimeSlotRequest
import com.rentmycar.requests.timeslot.TimeSlotUpdateRequest
import com.rentmycar.services.CarService
import com.rentmycar.services.TimeSlotService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.datetime.LocalDateTime
import com.rentmycar.services.PhysicsService
class TimeSlotController {


    private val timeSlotService = TimeSlotService()
    private val physicsService = PhysicsService()
    suspend fun createTimeSlot(call: ApplicationCall) {
        try {
            val user = call.user()

            val createTimeSlotRequest = call.receive<CreateTimeSlotRequest>()
            val validationErrors = createTimeSlotRequest.validate()
            val timeSlotRange = createTimeSlotRequest.availableFrom.rangeUntil(createTimeSlotRequest.availableUntil)

            if (validationErrors.isNotEmpty()) return call.respond(
                HttpStatusCode.BadRequest,
                "Invalid creation data: ${validationErrors.joinToString(", ")}"
            )

            timeSlotService.createTimeSlot(createTimeSlotRequest.carId, user, timeSlotRange)
            call.respond(HttpStatusCode.OK, "Timeslot created successfully")
        } catch (e: Exception) {
            return call.respond(
                HttpStatusCode.InternalServerError,
                e.message ?: "Internal Server Error"
            )
        }
    }

    suspend fun updateTimeSlot(call: ApplicationCall) {
        try {
            val user = call.user()
            val timeSlotUpdateRequest = call.receive<TimeSlotUpdateRequest>()
            val validationErrors = timeSlotUpdateRequest.validate()

            if (validationErrors.isNotEmpty()) return call.respond(
                HttpStatusCode.BadRequest,
                "Invalid update request: ${validationErrors.joinToString(", ")}"
            )

            timeSlotService.updateTimeSlot(
                timeSlotUpdateRequest.timeSlotId,
                user,
                timeSlotUpdateRequest.availableFrom,
                timeSlotUpdateRequest.availableUntil
            )
            call.respond(HttpStatusCode.OK, "Timeslot updated successfully")
        } catch (e: Exception) {
            return call.respond(
                HttpStatusCode.InternalServerError,
                e.message ?: "Internal Server Error"
            )
        }
    }

    suspend fun getTimeslotsByDateRange(call: RoutingCall) {
        try {
            val from = call.parameters["fromDate"] ?: return call.respond(
                HttpStatusCode.BadRequest,
                "From Date is missing or invalid"
            )
            val until = call.parameters["untilDate"] ?: return call.respond(
                HttpStatusCode.BadRequest,
                "Until Date is missing or invalid"
            )

            val timeSlotRange = LocalDateTime.parse(from).rangeUntil(LocalDateTime.parse(until))
            val timeslots = timeSlotService.getTimeSlots(timeSlotRange)
            call.respond(HttpStatusCode.OK, timeslots.map { it.toDTO() })
        } catch (e: Exception) {
            return call.respond(
                HttpStatusCode.InternalServerError,
                e.message ?: "Internal Server Error"
            )
        }
    }

    suspend fun getTimeslotById(call: RoutingCall) {
        try {
            val timeSlotId = call.parameters["id"]?.toIntOrNull() ?: return call.respond(
                HttpStatusCode.BadRequest,
                "Timeslot ID is missing or invalid"
            )

            val timeslot = timeSlotService.getTimeSlot(timeSlotId)
            call.respond(HttpStatusCode.OK, timeslot.toDTO())
        } catch (e: Exception) {
            return call.respond(
                HttpStatusCode.InternalServerError,
                e.message ?: "Internal Server Error"
            )
        }
    }

    suspend fun getTimeslotsByCarId(call: RoutingCall) {
        try {
            val carId = call.parameters["carId"]?.toIntOrNull() ?: return call.respond(
                HttpStatusCode.BadRequest,
                "carId ID is missing or invalid"
            )

            val car = CarService().getCar(carId)
            val timeslots = timeSlotService.getTimeSlots(car)
            call.respond(HttpStatusCode.OK, timeslots.map { it.toDTO() })
        } catch (e: Exception) {
            return call.respond(
                HttpStatusCode.InternalServerError,
                e.message ?: "Internal Server Error"
            )
        }
    }

    suspend fun removeTimeSlot(call: ApplicationCall) {
        try {
            val user = call.user()
            val timeslotId = call.parameters["id"]?.toIntOrNull() ?: return call.respond(
                HttpStatusCode.BadRequest,
                "Timeslot ID is missing or invalid"
            )

            timeSlotService.deleteTimeSlot(timeslotId, user)
            call.respond(HttpStatusCode.OK, "Timeslot deleted")
        } catch (e: Exception) {
            return call.respond(
                HttpStatusCode.InternalServerError,
                e.message ?: "Internal Server Error"
            )
        }
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
package com.rentmycar.controllers

import com.rentmycar.entities.toDTO
import com.rentmycar.plugins.user
import com.rentmycar.requests.reservation.CreateReservationRequest
import com.rentmycar.services.ReservationService
import com.rentmycar.services.TimeSlotService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*

class ReservationController {
    private val reservationService = ReservationService()

    suspend fun createReservation(call: ApplicationCall) {
        try {
            val user = call.user()

            val createReservationRequest = call.receive<CreateReservationRequest>()
            val validationErrors = createReservationRequest.validate()

            if (validationErrors.isNotEmpty()) return call.respond(
                HttpStatusCode.BadRequest,
                "Invalid creation data: ${validationErrors.joinToString(", ")}"
            )

            reservationService.createReservation(user, createReservationRequest.timeslotId)
            call.respond(HttpStatusCode.OK, "reservation created successfully")
        } catch (e: Exception) {
            return call.respond(
                HttpStatusCode.InternalServerError,
                e.message ?: "Internal Server Error"
            )
        }
    }

    suspend fun getReservationForTimeSlot(call: ApplicationCall) {
        try {
            val timeSlotId = call.parameters["id"]?.toIntOrNull() ?: return call.respond(
                HttpStatusCode.BadRequest,
                "Timeslot ID is missing or invalid"
            )

            val timeSlot = TimeSlotService().getTimeSlot(timeSlotId)
            val reservation = reservationService.getReservation(timeSlot)
            call.respond(HttpStatusCode.OK, reservation.toDTO())
        } catch (e: Exception) {
            return call.respond(
                HttpStatusCode.InternalServerError,
                e.message ?: "Internal Server Error"
            )
        }
    }

    suspend fun getReservationsForUser(call: ApplicationCall) {
        try {
            val user = call.user()
            val reservations = reservationService.getReservations(user)
            call.respond(HttpStatusCode.OK, reservations.map { it.toDTO() })
        } catch (e: Exception) {
            return call.respond(
                HttpStatusCode.InternalServerError,
                e.message ?: "Internal Server Error"
            )
        }
    }

    suspend fun removeReservation(call: ApplicationCall) {
        try {
            val user = call.user()
            val reservationId = call.parameters["id"]?.toIntOrNull() ?: return call.respond(
                HttpStatusCode.BadRequest,
                "Reservation ID is missing or invalid"
            )

            reservationService.deleteReservation(user, reservationId)
            call.respond(HttpStatusCode.OK, "reservation removed successfully")
        } catch (e: Exception) {
            return call.respond(
                HttpStatusCode.InternalServerError,
                e.message ?: "Internal Server Error"
            )
        }
    }
}
package com.rentmycar.controllers

import com.rentmycar.entities.toDTO
import com.rentmycar.plugins.user
import com.rentmycar.dtos.requests.reservation.CreateReservationRequest
import com.rentmycar.services.ReservationService
import com.rentmycar.services.TimeSlotService
import com.rentmycar.utils.sanitizeId
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*

class ReservationController {
    private val reservationService = ReservationService()
    private val timeSlotService = TimeSlotService()
    suspend fun createReservation(call: ApplicationCall) {
        val user = call.user()

        val createReservationRequest = call.receive<CreateReservationRequest>()
        createReservationRequest.validate()

        reservationService.createReservation(user, createReservationRequest.timeslotId)
        call.respond(HttpStatusCode.OK, "reservation created successfully")
    }

    suspend fun getReservationForTimeSlot(call: ApplicationCall) {
        val timeSlotId = sanitizeId(call.parameters["id"])

        val timeSlot = timeSlotService.getTimeSlot(timeSlotId)
        val reservation = reservationService.getReservation(timeSlot)

        call.respond(HttpStatusCode.OK, reservation.toDTO())
    }

    suspend fun getReservationsForUser(call: ApplicationCall) {
        val user = call.user()

        val reservations = reservationService.getReservations(user)

        call.respond(HttpStatusCode.OK, reservations.map { it.toDTO() })
    }

    suspend fun removeReservation(call: ApplicationCall) {
        val user = call.user()
        val reservationId = sanitizeId(call.parameters["id"])

        reservationService.deleteReservation(user, reservationId)

        call.respond(HttpStatusCode.OK, "reservation removed successfully")
    }
}
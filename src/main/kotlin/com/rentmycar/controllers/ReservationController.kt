package com.rentmycar.controllers

import com.rentmycar.plugins.user
import com.rentmycar.repositories.ReservationRepository
import com.rentmycar.repositories.TimeSlotRepository
import com.rentmycar.requests.reservation.CreateReservationRequest
import com.rentmycar.requests.reservation.RemoveReservationRequest
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*

class ReservationController {
    private val reservationRepository = ReservationRepository()

    suspend fun createReservation(call: ApplicationCall) {
        val user = call.user()

        val createReservationRequest = call.receive<CreateReservationRequest>()
        val validationErrors = createReservationRequest.validate()

        if (validationErrors.isNotEmpty()) return call.respond(
            HttpStatusCode.BadRequest,
            "Invalid creation data: ${validationErrors.joinToString(", ")}"
        )

        if (reservationRepository.doesReservationExistByTimeSlot(createReservationRequest.timeslotId))
            return call.respond(
                HttpStatusCode.Conflict,
                "Timeslot is already reserved"
            )

        val timeslot =
            TimeSlotRepository().getTimeSlot(createReservationRequest.timeslotId) ?: return call.respond(
                HttpStatusCode.NotFound,
                "Timeslot does not exist"
            )

        reservationRepository.createReservation(user, timeslot)
        call.respond(HttpStatusCode.OK, "reservation created successfully")
    }

    suspend fun removeReservation(call: ApplicationCall) {
        val principal = call.principal<JWTPrincipal>()
        val userId = principal?.payload?.getClaim("id")?.asInt()

        val removeReservationRequest = call.receive<RemoveReservationRequest>()
        val validationErrors = removeReservationRequest.validate()

        if (validationErrors.isNotEmpty()) return call.respond(
            HttpStatusCode.BadRequest,
            "Invalid creation data: ${validationErrors.joinToString(", ")}"
        )

        val reservation =
            reservationRepository.getReservationById(removeReservationRequest.reservationId) ?: return call.respond(
                HttpStatusCode.NotFound,
                "Reservation does not exist"
            )

        if (userId != reservation.reservorId.value) return call.respond(
            HttpStatusCode.BadRequest,
            "user is not the owner of the reservation"
        )

        reservationRepository.removeReservation(reservation)
        call.respond(HttpStatusCode.OK, "reservation removed successfully")
    }
}
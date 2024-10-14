package com.rentmycar.services

import com.rentmycar.entities.*
import com.rentmycar.repositories.ReservationRepository
import com.rentmycar.services.exceptions.AlreadyExistsException
import com.rentmycar.services.exceptions.NotAllowedException
import io.ktor.server.plugins.*

class ReservationService {
    private val reservationRepository = ReservationRepository()

    fun createReservation(user: User, timeSlotId: Int) {
        val timeSlot = TimeSlotService().getTimeSlot(timeSlotId)

        if (reservationRepository.getReservation(timeSlot) != null)
            throw AlreadyExistsException("Timeslot is already reserved")

        reservationRepository.createReservation(user, timeSlot)
    }

    fun getReservation(id: Int): Reservation =
        reservationRepository.getReservation(id) ?: throw NotFoundException("Reservation with id: $id not found")

    fun getReservation(timeSlot: Timeslot): Reservation {
        return reservationRepository.getReservation(timeSlot)
            ?: throw NotFoundException("No Reservation found for timeSlot: ${timeSlot.toDTO().id}")
    }

    fun getReservations(user: User): List<Reservation> = reservationRepository.getReservations(user)

    fun deleteReservation(user: User, id: Int) {
        val reservation = getReservation(id)
        if (!isReservationOwner(user, reservation))
            throw NotAllowedException("user is not the owner of the reservation")

        reservationRepository.deleteReservation(reservation)
    }

    private fun isReservationOwner(user: User, reservation: Reservation): Boolean =
        user.id.value == reservation.toDTO().reservorId
}
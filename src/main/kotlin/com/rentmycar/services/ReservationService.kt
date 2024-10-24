package com.rentmycar.services

import com.rentmycar.dtos.requests.reservation.FinishReservationRequest
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

    fun getReservation(timeSlot: Timeslot): Reservation {
        return reservationRepository.getReservation(timeSlot)
            ?: throw NotFoundException("No Reservation found for timeSlot: ${timeSlot.toDTO().id}")
    }

    fun getReservations(user: User): List<Reservation> = reservationRepository.getReservations(user)

    fun getFinishedReservationsHistory(user: User): List<Reservation> =
        reservationRepository.getFinishedReservationsHistory(user)

    fun deleteReservation(user: User, id: Int) {
        val reservation = getReservation(id)
        if (!isReservationOwner(user, reservation))
            throw NotAllowedException("user is not the owner of the reservation")

        reservationRepository.deleteReservation(reservation)
    }

    fun finishReservation(user: User, request: FinishReservationRequest) {
        var reservation = getReservation(request.reservationId)
        if (!isReservationOwner(user, reservation))
            throw NotAllowedException("User is not the owner of the reservation.")
        if (reservation.score != null)
            throw BadRequestException("The reservation has already been finished.")

        // Finish the reservation (update the data related to the reservation).
        reservation = reservationRepository.finishReservation(reservation, request)

        // Update user's score based on the finished reservation's data.
        val updatedUser = UserService().updateUserScore(user, request.getScore())

        // Send notification if user deserves the reward.
        if (updatedUser.score in 70..100) {
            NotificationService().createUserRewardNotification(updatedUser, reservation)
        }
    }

    private fun getReservation(id: Int): Reservation =
        reservationRepository.getReservation(id) ?: throw NotFoundException("Reservation with id: $id not found")

    private fun isReservationOwner(user: User, reservation: Reservation): Boolean =
        user.id.value == reservation.toDTO().reservorId
}
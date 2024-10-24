package com.rentmycar.repositories

import com.rentmycar.dtos.requests.reservation.FinishReservationRequest
import com.rentmycar.entities.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.transactions.transaction

class ReservationRepository {
    fun createReservation(reservor: User, timeslot: Timeslot): Reservation = transaction {
        Reservation.new {
            this.reservor = reservor
            this.timeslot = timeslot
        }
    }

    fun getReservation(reservationId: Int): Reservation? = transaction {
        Reservation.find(Reservations.id eq reservationId).singleOrNull()
    }

    fun getReservation(timeSlot: Timeslot): Reservation? = transaction {
        Reservation.find { Reservations.timeslot eq timeSlot.id }.singleOrNull()
    }

    fun getReservations(user: User): List<Reservation> = transaction {
        Reservation.find(Reservations.user eq user.id).toList()
    }

    fun finishReservation(reservation: Reservation, data: FinishReservationRequest) = transaction {
        reservation.apply {
            this.distance = data.distance
            this.averageAcceleration = data.averageAcceleration
            this.score = data.getScore()
        }
    }

    fun getFinishedReservationsHistory(user: User): List<Reservation> = transaction {
        Reservation.find {
            (Reservations.user eq user.id).and(Reservations.score.isNotNull())
        }.toList()
    }

    fun deleteReservation(reservation: Reservation) = transaction {
        Reservations.deleteWhere { Reservations.id eq reservation.id }
    }
}
package com.rentmycar.modules.users.reservations

import com.rentmycar.modules.availability.Timeslot
import com.rentmycar.modules.users.User
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.transactions.transaction

class ReservationRepository {
    fun createReservation(reservor: User, timeslot: Timeslot): Reservation {
        return transaction {
            Reservation.new {
                this.reservor = reservor
                this.timeslot = timeslot
            }
        }
    }

    fun removeReservation(reservation: Reservation) = transaction {
        Reservations.deleteWhere { Reservations.id eq reservation.id }
    }

    fun getReservationById(reservationId: Int): Reservation? = transaction {
        Reservation.find(Reservations.id eq reservationId).singleOrNull()
    }

    fun doesReservationExistByTimeSlot(reservationId: Int) = getReservationByTimeslotId(reservationId) != null

    private fun getReservationByTimeslotId(timeslotId: Int): Reservation? = transaction {
        Reservation.find { Reservations.timeslot eq timeslotId }.singleOrNull()
    }
}
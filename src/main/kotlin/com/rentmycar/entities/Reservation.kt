package com.rentmycar.entities

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.transactions.transaction

object Reservations : IntIdTable() {
    val user = reference("user_id", Users, ReferenceOption.CASCADE)
    val timeslot = reference("timeslot_id", Timeslots, ReferenceOption.CASCADE).uniqueIndex()
}

class Reservation(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Reservation>(Reservations)
    var reservor by User referencedOn Reservations.user
    var timeslot by Timeslot referencedOn Reservations.timeslot
}

@Serializable
data class ReservationDTO(
    val id: Int,
    val reservorId: Int,
    val timeslotId: Int
)

fun Reservation.toDTO(): ReservationDTO = transaction {
    ReservationDTO(
        id = this@toDTO.id.value,
        reservorId = this@toDTO.reservor.id.value,
        timeslotId = this@toDTO.timeslot.id.value
    )
}

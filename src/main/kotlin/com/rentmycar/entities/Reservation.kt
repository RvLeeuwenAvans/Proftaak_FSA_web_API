package com.rentmycar.entities

import com.rentmycar.dtos.ReservationDTO
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.transactions.transaction

object Reservations : IntIdTable() {
    val user = reference("user_id", Users, ReferenceOption.CASCADE)
    val timeslot = reference("timeslot_id", Timeslots, ReferenceOption.CASCADE).uniqueIndex()
    val averageAcceleration = double("average_acceleration").nullable()
    val distance = double("distance").nullable()
    val score = integer("score").nullable()
}

class Reservation(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Reservation>(Reservations)
    var reservor by User referencedOn Reservations.user
    var timeslot by Timeslot referencedOn Reservations.timeslot
    var averageAcceleration by Reservations.averageAcceleration
    var distance by Reservations.distance
    var score by Reservations.score
}

fun Reservation.toDTO(): ReservationDTO = transaction {
    ReservationDTO(
        id = this@toDTO.id.value,
        reservorId = this@toDTO.reservor.id.value,
        timeslotId = this@toDTO.timeslot.id.value,
        averageAcceleration = this@toDTO.averageAcceleration,
        distance = this@toDTO.distance,
        score = this@toDTO.score,
    )
}

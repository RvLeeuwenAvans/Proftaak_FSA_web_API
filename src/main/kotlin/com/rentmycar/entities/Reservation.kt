package com.rentmycar.entities

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

object Reservations : IntIdTable() {
    val userId = reference("user_id", Users)
    val timeslotId = reference("timeslot_id", Timeslots)
}

class Reservation(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Reservation>(Reservations)
    var userId by Reservations.userId
    var timeslotId by Reservations.timeslotId
}

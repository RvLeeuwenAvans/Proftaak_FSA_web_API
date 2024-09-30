package com.rentmycar.entities

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.ReferenceOption

object Reservations : IntIdTable() {
    val user = reference("user_id", Users, ReferenceOption.CASCADE)
    val timeslotId = reference("timeslot", Timeslots, ReferenceOption.CASCADE)
}

class Reservation(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Reservation>(Reservations)
    var reservorId by Reservations.user
    var reservor by Car referencedOn Reservations.user
    var timeslotId by Reservations.timeslotId
}

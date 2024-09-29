package com.rentmycar.entities

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.javatime.datetime

object Timeslots : IntIdTable() {
    val carId = reference("car", Cars, ReferenceOption.CASCADE)
    val availableFrom = datetime("available_from")
    val availableUntil = datetime("available_until")
}

class Timeslot(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Timeslot>(Timeslots)
    var carId by Car referencedOn Timeslots.carId
    var availableFrom by Timeslots.availableFrom
    var availableUntil by Timeslots.availableUntil
}

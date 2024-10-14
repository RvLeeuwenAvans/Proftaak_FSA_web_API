package com.rentmycar.entities

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toKotlinLocalDateTime
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.javatime.datetime
import org.jetbrains.exposed.sql.transactions.transaction

object Timeslots : IntIdTable() {
    val car = reference("car_id", Cars, ReferenceOption.CASCADE)
    val availableFrom = datetime("available_from")
    val availableUntil = datetime("available_until")
}

class Timeslot(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Timeslot>(Timeslots)

    var car by Car referencedOn Timeslots.car
    var availableFrom by Timeslots.availableFrom
    var availableUntil by Timeslots.availableUntil
}

@Serializable
data class TimeslotDTO(
    val id: Int,
    val carId: Int,
    val availableFrom: LocalDateTime,
    val availableUntil: LocalDateTime,
)

fun Timeslot.toDTO(): TimeslotDTO = transaction {
    TimeslotDTO(
        id = this@toDTO.id.value,
        carId = this@toDTO.car.id.value,
        availableFrom = this@toDTO.availableFrom.toKotlinLocalDateTime(),
        availableUntil = this@toDTO.availableUntil.toKotlinLocalDateTime()
    )
}

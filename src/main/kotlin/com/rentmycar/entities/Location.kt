package com.rentmycar.entities

import com.rentmycar.dtos.LocationDTO
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.transactions.transaction

object Locations : IntIdTable() {
    val car = reference("car_id", Cars, ReferenceOption.CASCADE).uniqueIndex()
    val longitude = double("longitude")
    val latitude = double("latitude")
}

class Location(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Location>(Locations)

    var car by Car referencedOn Locations.car
    var longitude by Locations.longitude
    var latitude by Locations.latitude
}

fun Location.toDTO(): LocationDTO = transaction {
    LocationDTO(
        id = this@toDTO.id.value,
        carId = this@toDTO.car.id.value,
        longitude = this@toDTO.longitude,
        latitude = this@toDTO.latitude
    )
}
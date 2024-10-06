package com.rentmycar.modules.cars.locations

import com.rentmycar.modules.cars.Car
import com.rentmycar.modules.cars.Cars
import org.jetbrains.exposed.dao.id.EntityID



import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.dao.*

object Locations : IntIdTable() {
    val car = reference("car_id", Cars)
    val longitude = float("longitude")
    val latitude = float("latitude")
}

class Location(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Location>(Locations)

    var car by Car referencedOn Locations.car
    var longitude by Locations.longitude
    var latitude by Locations.latitude
}

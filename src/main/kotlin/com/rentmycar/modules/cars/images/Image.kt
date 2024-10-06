package com.rentmycar.modules.cars.images

import com.rentmycar.modules.cars.Car
import com.rentmycar.modules.cars.Cars
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.dao.*
import org.jetbrains.exposed.dao.id.EntityID

object Images : IntIdTable() {
    val car = reference("car_id", Cars)
    val path = varchar("path", 255)
}

class Image(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Image>(Images)

    var car by Car referencedOn Images.car
    var path by Images.path
}

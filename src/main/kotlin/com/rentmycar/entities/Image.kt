package com.rentmycar.entities

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.dao.*
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.ReferenceOption

object Images : IntIdTable() {
    val car = reference("car_id", Cars, ReferenceOption.CASCADE)
    val path = varchar("path", 255)
}

class Image(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Image>(Images)

    var car by Car referencedOn Images.car
    var path by Images.path
}

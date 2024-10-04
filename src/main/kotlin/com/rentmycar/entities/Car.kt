package com.rentmycar.entities

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.ReferenceOption


object Cars : IntIdTable() {
    val user = reference("user_id", Users, ReferenceOption.CASCADE)
    val licensePlate = varchar("license_plate", 50).uniqueIndex()
    val model = reference("model_id", Models, onDelete = ReferenceOption.NO_ACTION)
    val fuel = reference("fuel_id", Fuels, onDelete = ReferenceOption.NO_ACTION)
    val year = integer("year")
    val color = varchar("color", 50)
    val transmission = varchar("transmission", 50)
}


class Car(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Car>(Cars)

    var owner by User referencedOn Cars.user
    var licensePlate by Cars.licensePlate
    var model by Model referencedOn Cars.model
    var fuel by Fuel referencedOn Cars.fuel
    var year by Cars.year
    var color by Cars.color
    var transmission by Cars.transmission


    val ownerId: Int
        get() = owner.id.value
}

package com.rentmycar.entities

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.ReferenceOption

object Cars : IntIdTable() {
    val user = reference("user_id", Users, ReferenceOption.CASCADE)
    val licensePlate = varchar("license_plate", 50).uniqueIndex()
}

class Car(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Car>(Cars)
    val ownerId by Cars.user
    var owner by User referencedOn Cars.user
    var licensePlate by Cars.licensePlate
}
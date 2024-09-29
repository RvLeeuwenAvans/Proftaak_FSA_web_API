package com.rentmycar.entities

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.ReferenceOption

object Cars : IntIdTable() {
    val user = reference("user", Users, ReferenceOption.CASCADE)
    val licenseplate = varchar("licenseplate", 50).uniqueIndex()
}

class Car(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Car>(Cars)
    var user by User referencedOn Cars.user
    var licenseplate by Cars.licenseplate
}



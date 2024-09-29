package com.rentmycar.entities

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

object Cars : IntIdTable() {
    val userId = reference("user_id", Users)
    val timeslotId = reference("timeslot_Id", Users).nullable()
    val liscenseplate = varchar("liscenseplate", 50).uniqueIndex()
}

class Car(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Car>(Cars)
    var userId by Cars.userId
    var timeslotId by Cars.timeslotId
    var liscenseplate by Cars.liscenseplate
}



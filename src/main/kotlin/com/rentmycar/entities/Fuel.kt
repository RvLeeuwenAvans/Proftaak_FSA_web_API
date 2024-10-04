package com.rentmycar.entities

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.dao.*
import org.jetbrains.exposed.dao.id.EntityID


object Fuels : IntIdTable() {
    val name = varchar("name", 50) // Name of the fuel type (e.g., Gasoline, Diesel, Electric)
}


class Fuel(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Fuel>(Fuels)

    var name by Fuels.name // Fuel type name
}

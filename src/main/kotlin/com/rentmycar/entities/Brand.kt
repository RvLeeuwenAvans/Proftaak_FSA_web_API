package com.rentmycar.entities

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.dao.*
import org.jetbrains.exposed.dao.id.EntityID

// Define the Brands table
object Brands : IntIdTable() {
    val name = varchar("name", 100)
}

// Define the Brand entity
class Brand(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Brand>(Brands)

    var name by Brands.name
}

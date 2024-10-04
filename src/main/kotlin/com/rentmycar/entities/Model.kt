package com.rentmycar.entities

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.dao.*
import org.jetbrains.exposed.dao.id.EntityID

// Define the Models table
object Models : IntIdTable() {
    val name = varchar("name", 50)
    val brand = reference("brand_id", Brands) // Relationship to Brand
}

// Define the Model entity
class Model(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Model>(Models)

    var name by Models.name
    var brand by Brand referencedOn Models.brand // Each model belongs to a brand
}

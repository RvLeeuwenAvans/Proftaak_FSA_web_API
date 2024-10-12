package com.rentmycar.entities

import com.rentmycar.responses.ModelDTO
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.dao.*
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.transactions.transaction

// Define the Models table
object Models : IntIdTable() {
    val name = varchar("name", 50)
    val brand = reference("brand_id", Brands, ReferenceOption.CASCADE)
}

// Define the Model entity
class Model(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Model>(Models)

    var name by Models.name
    var brand by Brand referencedOn Models.brand // Each model belongs to a brand
}

fun Model.toDTO(): ModelDTO = transaction {
    ModelDTO(
        id = this@toDTO.id.value,
        name = this@toDTO.name,
        brandId = this@toDTO.brand.id.value,
        brandName = this@toDTO.brand.name
    )
}
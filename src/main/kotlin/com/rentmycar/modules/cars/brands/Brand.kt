package com.rentmycar.modules.cars.brands

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.dao.*
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.transactions.transaction

// Define the Brands table
object Brands : IntIdTable() {
    val name = varchar("name", 100)
}

// Define the Brand entity
class Brand(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Brand>(Brands)

    var name by Brands.name
}

@Serializable
data class BrandDTO (
    val id: Int,
    val name: String,
)

fun Brand.toDTO() = transaction {
    BrandDTO(
        id = this@toDTO.id.value,
        name = this@toDTO.name
    )
}
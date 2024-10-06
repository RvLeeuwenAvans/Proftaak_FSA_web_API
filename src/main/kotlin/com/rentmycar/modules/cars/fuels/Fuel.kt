package com.rentmycar.modules.cars.fuels

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.dao.*
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.transactions.transaction


object Fuels : IntIdTable() {
    // TODO: can be enumeration: enumerationByName("name", 50, FuelType::class)
    val name = varchar("name", 50) // Name of the fuel type (e.g., Gasoline, Diesel, Electric)
}


class Fuel(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Fuel>(Fuels)

    var name by Fuels.name // Fuel type name
}

@Serializable
data class FuelDTO (
    val id: Int,
    val name: String,
)

fun Fuel.toDTO() : FuelDTO = transaction {
    FuelDTO(
        id = this@toDTO.id.value,
        name = this@toDTO.name
    )
}

package com.rentmycar.entities

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.transactions.transaction

// Cars table definition with appropriate references and relationships
object Cars : IntIdTable() {
    val user = reference("user_id", Users, ReferenceOption.CASCADE)        // Car owner, cascade delete when user is deleted
    val licensePlate = varchar("license_plate", 50).uniqueIndex()          // Unique license plate
    val model = reference("model_id", Models, onDelete = ReferenceOption.NO_ACTION)  // Reference to Model
    val fuel = reference("fuel_id", Fuels, onDelete = ReferenceOption.NO_ACTION)     // Reference to Fuel
    val year = integer("year")                                             // Car manufacturing year
    val color = varchar("color", 50)                                       // Car color
    // TODO: can be enumeration: enumerationByName("transmission", 50, Transmission::class)
    val transmission = varchar("transmission", 50)                         // Transmission type (e.g., automatic, manual)
    val price = double("price")
}

// Car entity class which defines the relationships and fields
class Car(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Car>(Cars)

    var owner by User referencedOn Cars.user                               // The owner (user) of the car
    var licensePlate by Cars.licensePlate                                  // Car's license plate
    var model by Model referencedOn Cars.model                             // Model of the car
    var fuel by Fuel referencedOn Cars.fuel                                // Fuel type (e.g., petrol, diesel)
    var year by Cars.year                                                  // Manufacturing year
    var color by Cars.color                                                // Color of the car
    var transmission by Cars.transmission                                  // Transmission type
    var price by Cars.price

    // Expose the owner's ID directly
    val ownerId by Cars.user
}

fun Car.toDTO(): CarDTO = transaction {
    CarDTO(
        id = this@toDTO.id.value,
        ownerId = this@toDTO.ownerId.value,
        licensePlate = this@toDTO.licensePlate,
        model = this@toDTO.model.name,
        fuel = this@toDTO.fuel.name,
        year = this@toDTO.year,
        color = this@toDTO.color,
        transmission = this@toDTO.transmission,
        price = this@toDTO.price
    )
}

@Serializable
data class CarDTO (
    val id: Int,
    val ownerId: Int,
    val licensePlate: String,
    val model: String,
    val fuel: String,
    val year: Int,
    val color: String,
    val transmission: String,
    val price: Double,
)
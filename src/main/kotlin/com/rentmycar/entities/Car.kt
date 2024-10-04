package com.rentmycar.entities

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.ReferenceOption

// Cars table definition with appropriate references and relationships
object Cars : IntIdTable() {
    val user = reference("user_id", Users, ReferenceOption.CASCADE)        // Car owner, cascade delete when user is deleted
    val licensePlate = varchar("license_plate", 50).uniqueIndex()          // Unique license plate
    val model = reference("model_id", Models, onDelete = ReferenceOption.NO_ACTION)  // Reference to Model
    val fuel = reference("fuel_id", Fuels, onDelete = ReferenceOption.NO_ACTION)     // Reference to Fuel
    val year = integer("year")                                             // Car manufacturing year
    val color = varchar("color", 50)                                       // Car color
    val transmission = varchar("transmission", 50)                         // Transmission type (e.g., automatic, manual)
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

    // Expose the owner's ID directly
    val ownerId: Int
        get() = owner.id.value  // This exposes the owner's ID directly as 'ownerId'
}

package com.rentmycar.entities

import com.rentmycar.dtos.CarDTO
import com.rentmycar.utils.Category
import com.rentmycar.utils.FuelType
import com.rentmycar.utils.Transmission
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.transactions.transaction

// Cars table definition with appropriate references and relationships
object Cars : IntIdTable() {
    val user = reference("user_id", Users, ReferenceOption.CASCADE)
    val model = reference("model_id", Models, onDelete = ReferenceOption.NO_ACTION)
    val location = optReference("location_id", Locations, onDelete = ReferenceOption.NO_ACTION).uniqueIndex()
    val licensePlate = varchar("license_plate", 50).uniqueIndex()
    val year = integer("year")
    val color = varchar("color", 50)
    val price = double("price")
    val transmission = enumerationByName("transmission", 50, Transmission::class)
    val fuel = enumerationByName("fuel", 50, FuelType::class)
    val category = enumerationByName("category", 50, Category::class)
}

// Car entity class which defines the relationships and fields
class Car(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Car>(Cars)

    var owner by User referencedOn Cars.user                               // The owner (user) of the car
    var model by Model referencedOn Cars.model                             // Model of the car
    var location by Location optionalReferencedOn Cars.location
    var licensePlate by Cars.licensePlate                                  // Car's license plate
    var year by Cars.year                                                  // Manufacturing year
    var color by Cars.color                                                // Color of the car
    var price by Cars.price
    var transmission by Cars.transmission                                  // Transmission type
    var fuel by Cars.fuel
    var category by Cars.category
}

fun Car.toDTO(): CarDTO = transaction {
    CarDTO(
        id = this@toDTO.id.value,
        ownerId = this@toDTO.owner.id.value,
        ownerName = this@toDTO.owner.username,
        ownerEmail = this@toDTO.owner.email,
        locationId = this@toDTO.location?.id?.value,
        licensePlate = this@toDTO.licensePlate,
        model = this@toDTO.model.name,
        brand = this@toDTO.model.brand.name,
        fuel = this@toDTO.fuel,
        year = this@toDTO.year,
        color = this@toDTO.color,
        transmission = this@toDTO.transmission,
        price = this@toDTO.price,
        category = this@toDTO.category,
    )
}


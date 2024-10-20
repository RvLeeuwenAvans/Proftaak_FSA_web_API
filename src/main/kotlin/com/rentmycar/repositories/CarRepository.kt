package com.rentmycar.repositories

import com.rentmycar.entities.Car
import com.rentmycar.entities.Cars
import com.rentmycar.entities.Model
import com.rentmycar.entities.User
import com.rentmycar.utils.Category
import com.rentmycar.utils.FuelType
import com.rentmycar.utils.LocationData
import com.rentmycar.utils.Transmission
import com.rentmycar.utils.haversine
import io.ktor.server.plugins.*
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction

class CarRepository {

    fun getCarById(carId: Int): Car = transaction {
        Car.find { Cars.id eq carId }.singleOrNull() ?: throw NotFoundException("Car with id $carId not found")
    }

    fun registerCar(
        owner: User,
        licensePlate: String,
        model: Model,
        fuel: String,
        year: Int,
        color: String,
        transmission: String,
        price: Double? = 0.0
    ): Car = transaction {
        Car.new {
            this.owner = owner
            this.licensePlate = licensePlate
            this.model = model
            this.fuel = FuelType.valueOf(fuel.uppercase())
            this.year = year
            this.color = color
            this.transmission = Transmission.valueOf(transmission.uppercase())
            this.price = price ?: 0.0
            this.category = FuelType.valueOf(fuel.uppercase()).category
        }
    }

    fun updateCar(
        id: Int,
        year: Int? = null,
        color: String? = null,
        transmission: String? = null,
        price: Double? = null,
        fuel: String? = null,
    ): Car = transaction {
        val car = getCarById(id)

        car.apply {
            year?.let { this.year = it }
            color?.let { this.color = it }
            transmission?.let { this.transmission = Transmission.valueOf(it) }
            price?.let { this.price = it }
            fuel?.let { this.fuel = FuelType.valueOf(it) }
        }
    }

    fun deleteCar(id: Int) = transaction { getCarById(id).delete() }

    fun getFilteredCars(
        ownerId: Int? = null,
        category: String? = null,
        minPrice: Int? = null,
        maxPrice: Int? = null,
        locationData: LocationData? = null
    ): List<Car> = transaction {
        Car.find {
            var conditions: Op<Boolean> = Op.TRUE

            // Filter by owner.
            ownerId?.let {
                conditions = conditions and (Cars.user eq it)
            }

            // Filter by category.
            category?.let {
                conditions = conditions and (Cars.category eq Category.valueOf(it.uppercase()))
            }

            // Filter by price range.
            if (minPrice != null && maxPrice != null) {
                conditions = conditions and (Cars.price.between(minPrice.toDouble(), maxPrice.toDouble()))
            } else if (minPrice != null) {
                conditions = conditions and (Cars.price greaterEq minPrice.toDouble())
            } else if (maxPrice != null) {
                conditions = conditions and (Cars.price lessEq maxPrice.toDouble())
            }

            conditions
        }.toList().filter { car ->
            // Filter by radius.
            // TODO: test this part as soon as https://proftaakfsa1.atlassian.net/browse/KAN-77 is delivered (for testing convenience).
            if (locationData == null) return@filter true
            val carLocation = LocationRepository().getByCar(car.id.value)

            val distance = haversine(
                locationData.latitude,
                locationData.longitude,
                carLocation.latitude,
                carLocation.longitude
            )
            return@filter distance <= locationData.radius.toDouble()
        }
    }

    fun getCarByLicensePlate(licensePlate: String): Car? = transaction {
        Car.find { Cars.licensePlate eq licensePlate }.singleOrNull()
    }

    fun getUserCarById(carId: Int, userId: EntityID<Int>): Car = transaction {
        Car.find { (Cars.id eq carId) and (Cars.user eq userId) }.singleOrNull()
            ?: throw NotFoundException("Car with id $carId not found")
    }
}

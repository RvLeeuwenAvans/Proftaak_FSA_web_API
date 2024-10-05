package com.rentmycar.repositories

import com.rentmycar.entities.Car
import com.rentmycar.entities.Cars
import com.rentmycar.entities.Model
import com.rentmycar.entities.Fuel
import com.rentmycar.entities.User
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction

class CarRepository {

    // Fetch a car by its ID
    fun getCarById(carId: Int): Car? = transaction {
        Car.find { Cars.id eq carId }.singleOrNull()
    }

    // Register a new car
    fun registerCar(
        owner: User,
        licensePlate: String,
        model: Model,
        fuel: Fuel,
        year: Int,
        color: String,
        transmission: String,
        price: Double? = 0.0
    ): Car = transaction {
        Car.new {
            this.owner = owner
            this.licensePlate = licensePlate
            this.model = model
            this.fuel = fuel
            this.year = year
            this.color = color
            this.transmission = transmission
            this.price = price ?: 0.0
        }
    }

    fun updateCar(
        id: Int,
        year: Int? = null,
        color: String? = null,
        transmission: String? = null,
        price: Double?= null
    ): Car? = transaction {
        val car = getCarById(id)

        car?.apply {
            year?.let { this.year = it }
            color?.let { this.color = it }
            transmission?.let { this.transmission = it }
            price?.let { this.price = it }
        }

        return@transaction car
    }

    fun deleteCar(id: Int) = transaction {
        val car = getCarById(id)
        car?.delete()
    }

    // TODO: Part of the epic link: https://proftaakfsa1.atlassian.net/browse/KAN-28
    fun getFilteredCars(
        ownerId: Int? = null,
    ): List<Car> = transaction {
        Car.find {
            var conditions: Op<Boolean> = Op.TRUE

            ownerId?.let {
                conditions = conditions and (Cars.user eq it)
            }

            // TODO: timeslot
            // TODO: location
            // TODO: hasImages?
            // TODO: year (range)
            // TODO: price (range)
            // TODO: category / fuel types
            // TODO: transmission
            // TODO: model

            conditions
        }.toList()
    }

    // Check if a license plate already exists
    fun doesLicensePlateExist(licensePlate: String) = getCarByLicensePlate(licensePlate) != null

    // Fetch a car by its license plate
    private fun getCarByLicensePlate(licensePlate: String): Car? = transaction {
        Car.find { Cars.licensePlate eq licensePlate }.singleOrNull()
    }
}

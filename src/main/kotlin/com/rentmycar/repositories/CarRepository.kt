package com.rentmycar.repositories

import com.rentmycar.entities.Car
import com.rentmycar.entities.Cars
import com.rentmycar.entities.Model
import com.rentmycar.entities.User
import com.rentmycar.utils.FuelType
import com.rentmycar.utils.Transmission
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
            this.fuel = FuelType.valueOf(fuel)
            this.year = year
            this.color = color
            this.transmission = Transmission.valueOf(transmission)
            this.price = price ?: 0.0
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

    // TODO: Part of the epic link: https://proftaakfsa1.atlassian.net/browse/KAN-30
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

    fun getCarByLicensePlate(licensePlate: String): Car? = transaction {
        Car.find { Cars.licensePlate eq licensePlate }.singleOrNull()
    }

    fun getUserCarById(carId: Int, userId: EntityID<Int>): Car = transaction {
        Car.find { (Cars.id eq carId) and (Cars.user eq userId) }.singleOrNull()
            ?: throw NotFoundException("Car with id $carId not found")
    }
}

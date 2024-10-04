package com.rentmycar.repositories

import com.rentmycar.entities.Car
import com.rentmycar.entities.Cars
import com.rentmycar.entities.Model
import com.rentmycar.entities.Fuel
import com.rentmycar.entities.User
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
        transmission: String
    ): Car = transaction {
        Car.new {
            this.owner = owner
            this.licensePlate = licensePlate
            this.model = model
            this.fuel = fuel
            this.year = year
            this.color = color
            this.transmission = transmission
        }
    }

    // Check if a license plate already exists
    fun doesLicensePlateExist(licensePlate: String) = getCarByLicensePlate(licensePlate) != null

    // Fetch a car by its license plate
    private fun getCarByLicensePlate(licensePlate: String): Car? = transaction {
        Car.find { Cars.licensePlate eq licensePlate }.singleOrNull()
    }
}

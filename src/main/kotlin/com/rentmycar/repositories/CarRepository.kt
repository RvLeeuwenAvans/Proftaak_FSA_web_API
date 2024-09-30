package com.rentmycar.repositories

import com.rentmycar.entities.Car
import com.rentmycar.entities.Cars
import com.rentmycar.entities.User
import org.jetbrains.exposed.sql.transactions.transaction

class CarRepository {
    fun getCarById(carId: Int): Car? = transaction {
        Car.find { Cars.id eq carId }.singleOrNull()
    }

    fun registerCar(owner: User, carLicensePlate: String): Car = transaction {
        Car.new {
            this.owner = owner
            licensePlate = carLicensePlate
        }
    }

    fun doesLicensePlateExist(licensePlate: String) = getCarByLicensePlate(licensePlate) != null

    private fun getCarByLicensePlate(licensePlate: String): Car? = transaction {
        Car.find { Cars.licensePlate eq licensePlate }.singleOrNull()
    }
}
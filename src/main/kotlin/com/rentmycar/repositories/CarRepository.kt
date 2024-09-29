package com.rentmycar.repositories

import com.rentmycar.entities.Car
import com.rentmycar.entities.Cars
import com.rentmycar.entities.User
import org.jetbrains.exposed.sql.transactions.transaction

class CarRepository {

    fun getCarsByUser(user: User): List<Car> {
        return transaction {
            Car.find { Cars.userId eq user.id }.toList()
        }
    }

    fun getCarById(carId: Int): Car? {
        return transaction {
            Car.find { Cars.id eq carId }.singleOrNull()
        }
    }

    fun registerCar(owner: User, carLicensePlate: String): Car {
        return transaction {
            Car.new {
                userId = owner
                licensePlate = carLicensePlate
            }
        }
    }

    fun doesLicensePlateExist(licensePlate: String): Boolean {
        return getCarByLicensePlate(licensePlate) != null
    }

    private fun getCarByLicensePlate(licensePlate: String): Car? {
        return transaction {
            Car.find { Cars.licensePlate eq licensePlate }.singleOrNull()
        }
    }
}
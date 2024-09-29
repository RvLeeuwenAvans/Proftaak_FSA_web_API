package com.rentmycar.repositories

import com.rentmycar.entities.Car
import com.rentmycar.entities.Cars
import com.rentmycar.entities.User
import org.jetbrains.exposed.sql.transactions.transaction

class CarRepository {

    fun getCarsByUser(user: User): List<Car> {
        return transaction {
            Car.find { Cars.user eq user.id }.toList()
        }
    }

    fun getCarById(carId: Int): Car? {
        return transaction {
            Car.find { Cars.id eq carId }.singleOrNull()
        }
    }

    private fun getCarByLicensePlate(licensePlate: String): Car? {
        return transaction {
            Car.find { Cars.licensePlate eq licensePlate }.singleOrNull()
        }
    }

    fun registerCar(owner: User, carlicensePlate: String): Car {
        return transaction {
            Car.new {
                user = owner
                licensePlate = carlicensePlate
            }
        }
    }

    fun doesLicensePlateExist(licensePlate: String): Boolean {
        return getCarByLicensePlate(licensePlate) != null
    }
}
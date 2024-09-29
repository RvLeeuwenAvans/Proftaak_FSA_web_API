package com.rentmycar.repositories

import com.rentmycar.entities.Car
import com.rentmycar.entities.Cars
import com.rentmycar.requests.RegistrationRequest
import org.jetbrains.exposed.sql.transactions.transaction

class CarRepository {

    fun getCarsByUserId(userId: Int): List<Car> {
        return transaction {
            Car.find { Cars.userId eq userId }.toList()
        }
    }

    fun getCarById(carId: Int): Car? {
        return transaction {
            Car.find { Cars.id eq carId }.singleOrNull()
        }
    }

    private fun getCarByTimeslotId(timeslotId: Int): Car? {
        return transaction {
            Car.find { Cars.timeslotId eq timeslotId }.singleOrNull()
        }
    }

    fun createCar(request: RegistrationRequest): Car {
        return transaction {
            Car.new {
            }
        }
    }
}
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

    private fun getCarByLicenseplate(licenseplate: String): Car? {
        return transaction {
            Car.find { Cars.licenseplate eq licenseplate }.singleOrNull()
        }
    }

    fun insertCar(car: Car): Car {
        return transaction {
            Car.new {
                user = car.user
                licenseplate = car.licenseplate
            }
        }
    }

    fun doesLicenseplateExist(licenseplate: String): Boolean {
        return getCarByLicenseplate(licenseplate) != null
    }
}
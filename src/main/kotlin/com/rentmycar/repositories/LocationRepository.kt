package com.rentmycar.repositories

import com.rentmycar.entities.*
import com.rentmycar.services.exceptions.NotFoundException
import org.jetbrains.exposed.sql.transactions.transaction

class LocationRepository {
    fun getByCar(carId: Int): Location = transaction {
        Location.find { Locations.car eq carId }.singleOrNull() ?: throw NotFoundException("Car location not found")
    }

    fun createLocation(car: Car, latitude: Double, longitude: Double): Location = transaction {
        Location.new {
            this.car = car
            this.latitude = latitude
            this.longitude = longitude
        }
    }

    fun updateLocation(carId: Int, latitude: Double, longitude: Double): Location = transaction {
        val location = getByCar(carId)

        location.apply {
            this.latitude = latitude
            this.longitude = longitude
        }
    }
}
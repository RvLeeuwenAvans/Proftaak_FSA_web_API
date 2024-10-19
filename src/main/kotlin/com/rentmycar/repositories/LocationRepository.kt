package com.rentmycar.repositories

import com.rentmycar.entities.*
import org.jetbrains.exposed.sql.transactions.transaction

class LocationRepository {
    fun getByCar(carId: Int): Location = transaction {
        Location.find { Locations.car eq carId }.first()
    }
}
package com.rentmycar.repositories



import com.rentmycar.entities.Fuel
import com.rentmycar.entities.Fuels
import org.jetbrains.exposed.sql.transactions.transaction

class FuelRepository {

    fun getFuelById(fuelId: Int): Fuel? = transaction {
        Fuel.findById(fuelId)
    }
}

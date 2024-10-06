package com.rentmycar.modules.cars.fuels



import org.jetbrains.exposed.sql.transactions.transaction

class FuelRepository {
    fun getFuel(fuelId: Int): Fuel? = transaction {
        Fuel.findById(fuelId)
    }

    fun getAllFuels(): List<Fuel> = transaction {
        Fuel.all().toList()
    }
}

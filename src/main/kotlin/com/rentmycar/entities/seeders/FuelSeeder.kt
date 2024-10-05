package com.rentmycar.entities.seeders

import com.rentmycar.entities.Fuels
import com.rentmycar.utils.FuelType.Companion.fuelTypes
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

/**
 * Seed Fuels table with default values.
 */
fun seedFuels() = transaction {
    for (value in fuelTypes) {
        val exists = Fuels.select(Fuels.id).where { Fuels.name eq value }.singleOrNull()

        if (exists == null) {
            Fuels.insert {
                it[name] = value
            }
        }
    }
}
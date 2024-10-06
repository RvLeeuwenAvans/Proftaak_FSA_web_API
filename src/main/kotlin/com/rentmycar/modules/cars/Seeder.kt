package com.rentmycar.modules.cars

import com.rentmycar.modules.cars.brands.Brands
import com.rentmycar.modules.cars.brands.models.Models
import com.rentmycar.modules.cars.fuels.Fuels
import com.rentmycar.utils.FuelType.Companion.fuelTypes
import com.rentmycar.utils.brands
import com.rentmycar.utils.models
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

/**
 * Seed Brands and Models tables with default values.
 */
fun seedBrandsAndModels() = transaction {
    // Seed brands.
    val brandsMap = mutableMapOf<String, Int>()
    for (brand in brands) {
        val exists = Brands.selectAll().where { Brands.name eq brand }.singleOrNull()

        val brandId = if (exists == null) {
            Brands.insertAndGetId { it[name] = brand }.value
        } else {
            exists[Brands.id].value
        }

        brandsMap[brand] = brandId
    }

    // Seed models.
    for ((modelName, brandName) in models) {
        val brandId = brandsMap[brandName]
        val exists = Models.select(Models.id).where { Models.name eq modelName }.singleOrNull()

        if (exists == null) {
            Models.insert {
                it[name] = modelName
                it[brand] = brandId!!
            }
        }
    }
}

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
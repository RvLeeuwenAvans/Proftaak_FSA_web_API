package com.rentmycar.modules.cars.brands

import org.jetbrains.exposed.sql.transactions.transaction

class BrandRepository {
    fun getAllBrands(): List<Brand> = transaction {
        Brand.all().toList()
    }
}
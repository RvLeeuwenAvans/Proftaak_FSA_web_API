package com.rentmycar.repositories

import com.rentmycar.entities.Brand
import org.jetbrains.exposed.sql.transactions.transaction

class BrandRepository {
    fun getAllBrands(): List<Brand> = transaction {
        Brand.all().toList()
    }
}
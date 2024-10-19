package com.rentmycar.repositories

import com.rentmycar.entities.Brand
import com.rentmycar.entities.Brands
import org.jetbrains.exposed.sql.transactions.transaction

class BrandRepository {
    fun getBrand(id: Int) = transaction {
        Brand.find { Brands.id eq id }.singleOrNull()
    }
    fun getBrand(id: Int?) = transaction {
        if (id == null) return@transaction null
        Brand.find { Brands.id eq id }.singleOrNull()
    }
    fun getBrand(name: String) = transaction {
        Brand.find { Brands.name eq name }.singleOrNull()
    }

    fun createBrand(name: String): Brand = transaction {
        Brand.new {
            this.name = name
        }
    }

    fun updateBrand(id: Int, name: String): Brand? = transaction {
        val brand = getBrand(id)

        brand?.apply {
            this.name = name
        }

        return@transaction brand
    }

    fun deleteBrand(id: Int) = transaction {
        val brand = getBrand(id)
        brand?.delete()
    }

    fun getAllBrands(): List<Brand> = transaction {
        Brand.all().toList()
    }

    fun doesBrandExist(name: String) = getBrand(name) != null
    fun doesBrandExist(id: Int) = getBrand(id) != null
}
package com.rentmycar.repositories

import com.rentmycar.entities.Brand
import com.rentmycar.entities.Brands
import com.rentmycar.services.exceptions.NotFoundException
import org.jetbrains.exposed.sql.transactions.transaction


class BrandRepository {

    fun getBrandById(id: Int): Brand = transaction {
        Brand.findById(id) ?: throw NotFoundException("Brand with id $id not found")
    }


    fun createBrand(name: String): Brand = transaction {
        Brand.new {
            this.name = name
        }
    }

    fun updateBrand(id: Int, name: String): Brand = transaction {
        val brand = Brand.findById(id) ?: throw NotFoundException("Brand with id $id not found")
        brand.name = name
        brand
    }

    fun deleteBrand(id: Int): Boolean = transaction {
        val brand = Brand.findById(id) ?: throw NotFoundException("Brand with id $id not found")
        brand.delete()
        true
    }

    fun getAllBrands(): List<Brand> = transaction {
        Brand.all().toList()
    }

    fun doesBrandExistByName(name: String): Boolean = transaction {
        Brand.find { Brands.name eq name }.singleOrNull() != null
    }
}
package com.rentmycar.services

import com.rentmycar.entities.Brand
import com.rentmycar.repositories.BrandRepository
import com.rentmycar.services.exceptions.AlreadyExistsException

class BrandService {
    private val brandRepository = BrandRepository()
    fun get(id: Int): Brand = brandRepository.getBrandById(id)
    fun create(name: String): Brand = brandRepository.createBrand(name)
    fun update(id: Int, name: String): Brand = brandRepository.updateBrand(id, name)
    fun delete(id: Int) = brandRepository.deleteBrand(id)

    fun validateExists(brandName: String) {
        if (brandRepository.doesBrandExistByName(brandName)) {
            throw AlreadyExistsException("There is already an already brand with name $brandName")
        }
    }

    fun getAll(): List<Brand> = brandRepository.getAllBrands()
}
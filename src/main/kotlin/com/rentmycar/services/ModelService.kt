package com.rentmycar.services

import com.rentmycar.entities.Model
import com.rentmycar.repositories.ModelRepository

class ModelService {
    private val modelRepository = ModelRepository()

    fun get(id: Int): Model = modelRepository.getModel(id)
    fun getAll(): List<Model> = modelRepository.getAllModels()
    fun getByBrand(id: Int): List<Model> = modelRepository.getModelsByBrand(id)
    fun create(name: String, brandId: Int) {
        val brand = BrandService().get(brandId)
        modelRepository.createModel(name, brand)
    }

    fun update(id: Int, name: String, brandId: Int) {
        val brand = BrandService().get(brandId)
        modelRepository.updateModel(id, name, brand)
    }

    fun delete(modelId: Int) = modelRepository.deleteModel(modelId)
}
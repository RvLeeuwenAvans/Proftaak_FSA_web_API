package com.rentmycar.repositories



import com.rentmycar.entities.Brand
import com.rentmycar.entities.Model
import com.rentmycar.entities.Models
import org.jetbrains.exposed.sql.transactions.transaction

class ModelRepository {
    fun getModel(modelId: Int): Model? = transaction {
        Model.findById(modelId)
    }

    fun getAllModels(): List<Model> = transaction {
        Model.all().toList()
    }

    fun getModelsByBrand(brandId: Int): List<Model> = transaction {
        Model.find { Models.brand eq brandId }.toList()
    }

    fun doesBrandContainModel(name: String, brandId: Int): Boolean = transaction {
        val modelsByBrand = getModelsByBrand(brandId)
        (modelsByBrand.map { it.name }.contains(name))
    }
    fun doesBrandContainModel(modelId: Int, brandId: Int): Boolean = transaction {
        val modelsByBrand = getModelsByBrand(brandId)
        (modelsByBrand.map { it.id.value }.contains(modelId))
    }

    fun createModel(name: String, brand: Brand): Model = transaction {
        Model.new {
            this.name = name
            this.brand = brand
        }
    }

    fun updateModel(id: Int, name: String? = null, brand: Brand? = null): Model? = transaction {
        val model = getModel(id)

        model?.apply {
            name?.let { this.name = it }
            brand?.let { this.brand = it }
        }

        return@transaction model
    }

    fun deleteModel(id: Int) = transaction {
        val model = getModel(id)
        model?.delete()
    }

    fun doesModelExist(id: Int) = getModel(id) != null
}

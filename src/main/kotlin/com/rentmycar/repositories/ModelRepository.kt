package com.rentmycar.repositories


import com.rentmycar.entities.Brand
import com.rentmycar.entities.Model
import com.rentmycar.entities.Models
import io.ktor.server.plugins.*
import org.jetbrains.exposed.sql.transactions.transaction

class ModelRepository {
    fun getModel(modelId: Int): Model = transaction {
        Model.findById(modelId) ?: throw NotFoundException("Model with id $id not found")
    }

    fun getAllModels(): List<Model> = transaction {
        Model.all().toList()
    }

    fun getModelsByBrand(brandId: Int): List<Model> = transaction {
        Model.find { Models.brand eq brandId }.toList()
    }

    fun createModel(name: String, brand: Brand): Model = transaction {
        Model.new {
            this.name = name
            this.brand = brand
        }
    }

    fun updateModel(id: Int, name: String, brand: Brand): Model = transaction {
        val model = getModel(id)

        model.apply {
            name.let { this.name = it }
            brand.let { this.brand = it }
        }
    }

    fun deleteModel(id: Int) = transaction {
        val model = getModel(id)
        model.delete()
    }
}

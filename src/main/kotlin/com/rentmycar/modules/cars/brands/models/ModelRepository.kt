package com.rentmycar.modules.cars.brands.models



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
}

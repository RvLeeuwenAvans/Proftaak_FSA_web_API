package com.rentmycar.repositories



import com.rentmycar.entities.Model
import com.rentmycar.entities.Models
import org.jetbrains.exposed.sql.transactions.transaction

class ModelRepository {

    fun getModelById(modelId: Int): Model? = transaction {
        Model.findById(modelId)
    }
}

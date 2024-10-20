package com.rentmycar.controllers

import com.rentmycar.entities.toDTO
import com.rentmycar.requests.model.CreateModelRequest
import com.rentmycar.requests.model.UpdateModelRequest
import com.rentmycar.services.ModelService
import com.rentmycar.utils.sanitizeId
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*

class ModelController {

    suspend fun getAllModels(call: ApplicationCall) {
        val models = ModelService().getAll()

        return call.respond(
            HttpStatusCode.OK,
            models.map { it.toDTO() }
        )
    }

    suspend fun getModelsByBrand(call: ApplicationCall) {
        val id = sanitizeId(call.parameters["id"])

        val models = ModelService().getByBrand(id)

        return call.respond(
            HttpStatusCode.OK,
            models.map { it.toDTO() }
        )
    }

    suspend fun createModel(call: ApplicationCall) {
        val request = call.receive<CreateModelRequest>()
        request.validate()

        ModelService().create(request.name, request.brandId)

        return call.respond(HttpStatusCode.OK, "Model successfully created.")
    }

    suspend fun updateModel(call: ApplicationCall) {
        val request = call.receive<UpdateModelRequest>()
        request.validate()

        ModelService().update(request.id, request.name, request.brandId)

        return call.respond(HttpStatusCode.OK, "Model successfully updated.")
    }

    suspend fun deleteModel(call: ApplicationCall) {
        val modelId = sanitizeId(call.parameters["id"])

        ModelService().delete(modelId)

        return call.respond(HttpStatusCode.OK, "Model deleted successfully.")
    }
}
package com.rentmycar.routing.controllers

import com.rentmycar.entities.toDTO
import com.rentmycar.routing.controllers.requests.model.CreateModelRequest
import com.rentmycar.routing.controllers.requests.model.UpdateModelRequest
import com.rentmycar.services.ModelService
import com.rentmycar.utils.sanitizeId
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*

class ModelController {
    private val modelService = ModelService()

    suspend fun getAllModels(call: ApplicationCall) {
        val models = modelService.getAll()

        return call.respond(
            HttpStatusCode.OK,
            models.map { it.toDTO() }
        )
    }

    suspend fun getModelsByBrand(call: ApplicationCall) {
        val id = sanitizeId(call.parameters["id"])

        val models = modelService.getByBrand(id)

        return call.respond(
            HttpStatusCode.OK,
            models.map { it.toDTO() }
        )
    }

    suspend fun createModel(call: ApplicationCall) {
        val request = call.receive<CreateModelRequest>()
        request.validate()

        modelService.create(request.name, request.brandId)

        return call.respond(HttpStatusCode.OK, "Model successfully created.")
    }

    suspend fun updateModel(call: ApplicationCall) {
        val request = call.receive<UpdateModelRequest>()
        request.validate()

        modelService.update(request.id, request.name, request.brandId)

        return call.respond(HttpStatusCode.OK, "Model successfully updated.")
    }

    suspend fun deleteModel(call: ApplicationCall) {
        val modelId = sanitizeId(call.parameters["id"])

        modelService.delete(modelId)

        return call.respond(HttpStatusCode.OK, "Model deleted successfully.")
    }
}
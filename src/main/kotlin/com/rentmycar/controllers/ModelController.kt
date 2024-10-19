package com.rentmycar.controllers

import com.rentmycar.entities.Brand
import com.rentmycar.entities.toDTO
import com.rentmycar.repositories.BrandRepository
import com.rentmycar.repositories.ModelRepository
import com.rentmycar.requests.model.CreateModelRequest
import com.rentmycar.requests.model.UpdateModelRequest
import com.rentmycar.utils.sanitizeId
import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.application.*
import io.ktor.server.request.*

class ModelController {
    private val modelRepository = ModelRepository()
    private val brandRepository = BrandRepository()

    private fun validateRequest(name: String, brand: Brand?): String? {
        if (brand == null) return "Brand does not exist."
        else if (modelRepository.doesBrandContainModel(name = name, brandId = brand.id.value))
            return "Model is not unique."
        return null
    }
    private fun validateRequest(name: String, brandId: Int): String? {
        val brand = brandRepository.getBrand(brandId)
        if (brand == null) return "Brand does not exist."
        else if (modelRepository.doesBrandContainModel(name = name, brandId = brand.id.value))
            return "Model is not unique."
        return null
    }

    suspend fun getAllModels(call: ApplicationCall) {
        val models = modelRepository.getAllModels()

        return call.respond(
            HttpStatusCode.OK,
            models.map { it.toDTO() }
        )
    }

    suspend fun getModelsByBrand(call: ApplicationCall) {
        val id = sanitizeId(call.parameters["id"])

        if (id == -1) return call.respond(
            HttpStatusCode.BadRequest,
            "Owner ID is invalid."
        )

        val models = modelRepository.getModelsByBrand(id)

        return call.respond(
            HttpStatusCode.OK,
            models.map { it.toDTO() }
        )
    }

    suspend fun createModel(call: ApplicationCall) {
        val request = call.receive<CreateModelRequest>()
        val errors = request.validate()

        if (errors.isNotEmpty()) {
            call.respond(HttpStatusCode.BadRequest, "Invalid data: ${errors.joinToString(", ")}.")
        }

        val brand = brandRepository.getBrand(request.brandId)

        val validationError = validateRequest(request.name, brand)
        if (validationError != null) return call.respond(
            HttpStatusCode.BadRequest,
            validationError
        )

        modelRepository.createModel(
            name = request.name,
            brand = brand!!
        )

        return call.respond(HttpStatusCode.OK, "Model successfully created.")
    }

    suspend fun updateModel(call: ApplicationCall) {
        val request = call.receive<UpdateModelRequest>()
        val errors = request.validate()

        if (errors.isNotEmpty()) return call.respond(
            HttpStatusCode.BadRequest,
            "Invalid data: ${errors.joinToString(", ")}."
        )

        val brand = brandRepository.getBrand(request.newBrandId)
        val model = modelRepository.getModel(request.id)
        val modelName = model?.name ?: request.name ?: ""

        if (model == null || !modelRepository.doesBrandContainModel(request.id, request.brandId)) return call.respond(
            HttpStatusCode.NotFound,
            "Model with id ${request.id} does not exist or its brand ID is not ${request.brandId}."
        )

        if (request.name != null) {
            val validationResult = validateRequest(request.name, request.brandId)
            if (validationResult != null) return call.respond(
                HttpStatusCode.BadRequest,
                validationResult
            )
        }

        if (request.newBrandId != null) {
            val validationResult = validateRequest(modelName, request.newBrandId)
            if (validationResult != null) return call.respond(
                HttpStatusCode.BadRequest,
                validationResult
            )
        }

        modelRepository.updateModel(
            id = request.id,
            name = request.name,
            brand = brand
        )

        return call.respond(HttpStatusCode.OK, "Model successfully updated.")
    }

    suspend fun deleteModel(call: ApplicationCall) {
        val modelId = sanitizeId(call.parameters["id"])

        if (modelId == -1) return call.respond(
            HttpStatusCode.BadRequest,
            "Model ID is invalid."
        )

        if (!modelRepository.doesModelExist(modelId)) return call.respond(
            HttpStatusCode.NotFound,
            "Model with ID \"$modelId\" does not exist."
        )

        modelRepository.deleteModel(modelId)
        return call.respond(HttpStatusCode.OK, "Model deleted successfully.")
    }
}
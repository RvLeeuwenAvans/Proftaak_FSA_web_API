package com.rentmycar.controllers

import com.rentmycar.entities.ModelDTO
import com.rentmycar.entities.toDTO
import com.rentmycar.repositories.ModelRepository
import com.rentmycar.utils.isNumeric
import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.application.*

class ModelController {
    private val modelRepository = ModelRepository()

    /**
     * Get Int-typed id from String?-typed id.
     */
    private fun sanitizeId(id: String? = null): Int =
        if (id == null || !isNumeric(id)) -1 else id.toInt()

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
}
package com.rentmycar.controllers

import com.rentmycar.entities.toDTO
import com.rentmycar.repositories.BrandRepository
import com.rentmycar.requests.brand.CreateBrandRequest
import com.rentmycar.requests.brand.UpdateBrandRequest
import com.rentmycar.utils.sanitizeId
import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.application.*
import io.ktor.server.request.*

class BrandController {
    private val brandRepository = BrandRepository()

    private fun validateBrand(errors: List<String>, name: String): String? {
        if (errors.isNotEmpty()) return "Invalid data: ${errors.joinToString(", ")}."

        if (brandRepository.doesBrandExist(name))
            return "Brand with name \"${name}\" already exists."

        return null
    }

    suspend fun getAllBrands(call: ApplicationCall) {
        val brands = brandRepository.getAllBrands()

        return call.respond(
            HttpStatusCode.OK,
            brands.map { it.toDTO() }
        )
    }

    suspend fun createBrand(call: ApplicationCall) {
        val request = call.receive<CreateBrandRequest>()
        val errors = request.validate()

        val validationResult = validateBrand(errors, request.name)
        if (validationResult != null) return call.respond(
            HttpStatusCode.BadRequest,
            validationResult
        )

        brandRepository.createBrand(name = request.name)

        return call.respond(HttpStatusCode.OK, "Brand created successfully.")
    }

    suspend fun updateBrand(call: ApplicationCall) {
        val request = call.receive<UpdateBrandRequest>()
        val errors = request.validate()

        val validationResult = validateBrand(errors, request.name)
        if (validationResult != null) return call.respond(
            HttpStatusCode.BadRequest,
            validationResult
        )

        brandRepository.updateBrand(name = request.name, id = request.id)

        return call.respond(HttpStatusCode.OK, "Brand updated successfully.")
    }

    suspend fun deleteBrand(call: ApplicationCall) {
        val brandId = sanitizeId(call.parameters["id"])

        if (brandId == -1) return call.respond(
            HttpStatusCode.BadRequest,
            "Brand ID is invalid."
        )

        if (!brandRepository.doesBrandExist(brandId)) return call.respond(
            HttpStatusCode.NotFound,
            "Brand with ID \"$brandId\" does not exist."
        )

        brandRepository.deleteBrand(brandId)
        return call.respond(HttpStatusCode.OK, "Brand deleted successfully.")
    }
}
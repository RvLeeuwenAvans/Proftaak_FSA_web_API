package com.rentmycar.routing.controllers

import com.rentmycar.entities.toDTO
import com.rentmycar.routing.controllers.requests.brand.CreateBrandRequest
import com.rentmycar.routing.controllers.requests.brand.UpdateBrandRequest
import com.rentmycar.services.BrandService
import com.rentmycar.utils.sanitizeId
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*

class BrandController {
    private val brandService = BrandService()

    suspend fun getAllBrands(call: ApplicationCall) {
        val brands = brandService.getAll()

        return call.respond(
            HttpStatusCode.OK,
            brands.map { it.toDTO() }
        )
    }

    suspend fun createBrand(call: ApplicationCall) {
        val request = call.receive<CreateBrandRequest>()

        request.validate()
        brandService.validateExists(request.name)

        brandService.create(request.name)
        return call.respond(HttpStatusCode.OK, "Brand created successfully.")
    }

    suspend fun updateBrand(call: ApplicationCall) {
        val request = call.receive<UpdateBrandRequest>()

        request.validate()
        brandService.validateExists(request.name)

        brandService.update(request.id, request.name)

        return call.respond(HttpStatusCode.OK, "Brand updated successfully.")
    }

    suspend fun deleteBrand(call: ApplicationCall) {
        val brandId = sanitizeId(call.parameters["id"])

        brandService.delete(brandId)

        return call.respond(HttpStatusCode.OK, "Brand deleted successfully.")
    }
}
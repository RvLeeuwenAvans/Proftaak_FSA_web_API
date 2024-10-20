package com.rentmycar.controllers

import com.rentmycar.entities.toDTO
import com.rentmycar.requests.brand.CreateBrandRequest
import com.rentmycar.requests.brand.UpdateBrandRequest
import com.rentmycar.services.BrandService
import com.rentmycar.utils.sanitizeId
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*

class BrandController {

    suspend fun getAllBrands(call: ApplicationCall) {
        val brands = BrandService().getAll()

        return call.respond(
            HttpStatusCode.OK,
            brands.map { it.toDTO() }
        )
    }

    suspend fun createBrand(call: ApplicationCall) {
        val request = call.receive<CreateBrandRequest>()

        request.validate()
        BrandService().validateExists(request.name)

        BrandService().create(request.name)
        return call.respond(HttpStatusCode.OK, "Brand created successfully.")
    }

    suspend fun updateBrand(call: ApplicationCall) {
        val request = call.receive<UpdateBrandRequest>()

        request.validate()
        BrandService().validateExists(request.name)

        BrandService().update(request.id, request.name)

        return call.respond(HttpStatusCode.OK, "Brand updated successfully.")
    }

    suspend fun deleteBrand(call: ApplicationCall) {
        val brandId = sanitizeId(call.parameters["id"])

        BrandService().delete(brandId)

        return call.respond(HttpStatusCode.OK, "Brand deleted successfully.")
    }
}
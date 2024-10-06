package com.rentmycar.modules.cars.brands

import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.application.*

class BrandService {
    private val brandRepository = BrandRepository()

    suspend fun getAllBrands(call: ApplicationCall) {
        val brands = brandRepository.getAllBrands()

        return call.respond(
            HttpStatusCode.OK,
            brands.map { it.toDTO() }
        )
    }
}
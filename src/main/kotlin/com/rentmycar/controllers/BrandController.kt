package com.rentmycar.controllers

import com.rentmycar.entities.toDTO
import com.rentmycar.repositories.BrandRepository
import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.application.*

class BrandController {
    private val brandRepository = BrandRepository()

    suspend fun getAllBrands(call: ApplicationCall) {
        val brands = brandRepository.getAllBrands()

        return call.respond(
            HttpStatusCode.OK,
            brands.map { it.toDTO() }
        )
    }
}
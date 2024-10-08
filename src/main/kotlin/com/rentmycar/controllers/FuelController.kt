package com.rentmycar.controllers

import com.rentmycar.entities.toDTO
import com.rentmycar.repositories.FuelRepository
import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.application.*

class FuelController {
    private val fuelRepository = FuelRepository()

    suspend fun getAllFuels(call: ApplicationCall) {
        val fuels = fuelRepository.getAllFuels()

        return call.respond(
            HttpStatusCode.OK,
            fuels.map { it.toDTO() }
        )
    }
}
package com.rentmycar.modules.cars.fuels

import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.application.*

class FuelService {
    private val fuelRepository = FuelRepository()

    suspend fun getAllFuels(call: ApplicationCall) {
        val fuels = fuelRepository.getAllFuels()

        return call.respond(
            HttpStatusCode.OK,
            fuels.map { it.toDTO() }
        )
    }
}
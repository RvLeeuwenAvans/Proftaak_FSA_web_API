package com.rentmycar.controllers

import com.rentmycar.dtos.requests.location.LocationRequest
import com.rentmycar.entities.toDTO
import com.rentmycar.plugins.user
import com.rentmycar.services.LocationService
import com.rentmycar.utils.sanitizeId
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

class LocationController {
    private val locationService = LocationService()

    suspend fun getLocation(call: RoutingCall) {
        val carId = sanitizeId(call.parameters["id"])

        call.respond(HttpStatusCode.OK, LocationService().getByCar(carId).toDTO())
    }

    suspend fun addLocation(call: ApplicationCall) {
        val request = call.receive<LocationRequest>()
        request.validate()

        locationService.addLocation(call.user(), request)
        call.respond(HttpStatusCode.OK, "Location added successfully.")
    }

    suspend fun updateLocation(call: ApplicationCall) {
        val request = call.receive<LocationRequest>()
        request.validate()

        locationService.updateLocation(call.user(), request)
        call.respond(HttpStatusCode.OK, "Location updated successfully.")
    }
}
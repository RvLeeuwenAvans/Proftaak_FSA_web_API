package com.rentmycar.controllers

import com.rentmycar.requests.location.LocationRequest
import com.rentmycar.services.LocationService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*

class LocationController {
    private val locationService = LocationService()

    suspend fun addLocation(call: ApplicationCall) {
        val request = call.receive<LocationRequest>()
        request.validate()

        locationService.addLocation(request)
        call.respond(HttpStatusCode.OK, "Location added successfully.")
    }

    suspend fun updateLocation(call: ApplicationCall) {
        val request = call.receive<LocationRequest>()
        request.validate()

        locationService.updateLocation(request)
        call.respond(HttpStatusCode.OK, "Location updated successfully.")
    }
}
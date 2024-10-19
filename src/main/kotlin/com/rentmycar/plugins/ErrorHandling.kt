package com.rentmycar.plugins


import com.rentmycar.services.exceptions.RequestValidationException
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*

fun Application.configureErrorHandling() {
    install(StatusPages) {
        exception<NotFoundException> { call, cause ->
            call.respond(HttpStatusCode.NotFound, mapOf("error" to cause.message))
        }
        exception<RequestValidationException> {call, cause ->
            call.respond(HttpStatusCode.BadRequest, mapOf("errors" to cause.errors))
        }
    }
}
package com.rentmycar.routing.controllers.requests.model

import com.rentmycar.services.exceptions.RequestValidationException
import kotlinx.serialization.Serializable

@Serializable
data class CreateModelRequest(
    val name: String,
    val brandId: Int
) {
    fun validate() {
        val errors = mutableListOf<String>()

        if (name.isBlank()) errors.add("Model name cannot be blank")
        if (brandId < 0) errors.add("Brand ID cannot be negative")

        if (errors.isNotEmpty()) {
            throw RequestValidationException(errors)
        }
    }
}

package com.rentmycar.requests.model

import com.rentmycar.services.exceptions.RequestValidationException
import kotlinx.serialization.Serializable

@Serializable
data class UpdateModelRequest(
    val id: Int,
    val name: String,
    val brandId: Int,
) {
    fun validate() {
        val errors = mutableListOf<String>()

        if (id < 0) errors.add("Model ID cannot be negative")
        if (name.isBlank()) errors.add("Model name cannot be blank")
        if (brandId < 0) errors.add("Brand ID cannot be negative")

        if (errors.isNotEmpty()) {
            throw RequestValidationException(errors)
        }
    }
}

package com.rentmycar.dtos.requests.brand

import com.rentmycar.services.exceptions.RequestValidationException
import kotlinx.serialization.Serializable

@Serializable
data class UpdateBrandRequest(
    val name: String,
    val id: Int,
) {
    fun validate() {
        val errors = mutableListOf<String>()

        if (name.isBlank()) errors.add("Brand name cannot be blank")
        if (id < 0) errors.add("Brand ID cannot be negative")

        if (errors.isNotEmpty()) {
            throw RequestValidationException(errors)
        }
    }
}
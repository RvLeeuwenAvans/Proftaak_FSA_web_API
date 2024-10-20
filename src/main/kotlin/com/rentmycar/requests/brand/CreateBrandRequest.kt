package com.rentmycar.requests.brand

import com.rentmycar.services.exceptions.RequestValidationException
import kotlinx.serialization.Serializable

@Serializable
data class CreateBrandRequest(
    val name: String
) {
    fun validate() {
        val errors = mutableListOf<String>()

        if (name.isBlank()) errors.add("Brand name cannot be blank")

        if (errors.isNotEmpty()) {
            throw RequestValidationException(errors)
        }
    }
}
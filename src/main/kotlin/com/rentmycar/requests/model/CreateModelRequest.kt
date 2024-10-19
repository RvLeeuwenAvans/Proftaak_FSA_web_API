package com.rentmycar.requests.model

import kotlinx.serialization.Serializable

@Serializable
data class CreateModelRequest (
    val name: String,
    val brandId: Int
) {
    fun validate(): List<String> {
        val errors = mutableListOf<String>()

        if (name.isBlank()) errors.add("Model name cannot be blank")
        if (brandId < 0) errors.add("Brand ID cannot be negative")

        return errors
    }
}

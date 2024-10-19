package com.rentmycar.requests.model

import kotlinx.serialization.Serializable

@Serializable
data class UpdateModelRequest (
    val id: Int,
    val name: String? = null,
    val brandId: Int,
    val newBrandId: Int? = null
) {
    fun validate(): List<String> {
        val errors = mutableListOf<String>()

        if (id < 0) errors.add("Model ID cannot be negative")
        if (brandId < 0) errors.add("Brand ID cannot be negative")
        if (name != null && name.isBlank()) errors.add("Model name cannot be blank")
        if (newBrandId != null && newBrandId < 0) errors.add("New brand ID cannot be blank")

        return errors
    }
}

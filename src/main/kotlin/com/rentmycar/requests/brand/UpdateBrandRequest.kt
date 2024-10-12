package com.rentmycar.requests.brand

import kotlinx.serialization.Serializable

@Serializable
data class UpdateBrandRequest (
    val name: String,
    val id: Int,
) {
    fun validate(): List<String> {
        val errors = mutableListOf<String>()

        if (name.isBlank()) errors.add("Brand name cannot be blank")
        if (id < 0) errors.add("Brand ID cannot be negative")

        return errors
    }
}
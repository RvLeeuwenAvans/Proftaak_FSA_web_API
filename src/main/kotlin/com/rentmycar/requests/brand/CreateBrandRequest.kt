package com.rentmycar.requests.brand

import kotlinx.serialization.Serializable

@Serializable
data class CreateBrandRequest (
    val name: String
) {
    fun validate (): List<String> {
        val errors = mutableListOf<String>()

        if (name.isBlank()) errors.add("Brand name cannot be blank")

        return errors
    }
}
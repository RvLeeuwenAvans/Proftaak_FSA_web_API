package com.rentmycar.responses

import kotlinx.serialization.Serializable

@Serializable
data class BrandDTO (
    val id: Int,
    val name: String,
)

package com.rentmycar.controllers.responses

import kotlinx.serialization.Serializable

@Serializable
data class BrandDTO (
    val id: Int,
    val name: String,
)

package com.rentmycar.dtos

import kotlinx.serialization.Serializable

@Serializable
data class BrandDTO (
    val id: Int,
    val name: String,
)

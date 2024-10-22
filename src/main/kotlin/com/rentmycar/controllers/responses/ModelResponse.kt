package com.rentmycar.controllers.responses

import kotlinx.serialization.Serializable

@Serializable
data class ModelDTO(
    val id: Int,
    val name: String,
    val brandId: Int,
    val brandName: String,
)

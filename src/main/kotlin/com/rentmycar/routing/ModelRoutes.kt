package com.rentmycar.routing

import com.rentmycar.modules.cars.brands.models.ModelService
import io.ktor.server.auth.*
import io.ktor.server.routing.*

fun Route.modelRoutes() {
    val modelController = ModelService()
    val prefix = "/model"

    authenticate {
        get("$prefix/all") { modelController.getAllModels(call) }
        get("$prefix/brand/{id}") { modelController.getModelsByBrand(call) }
    }
}
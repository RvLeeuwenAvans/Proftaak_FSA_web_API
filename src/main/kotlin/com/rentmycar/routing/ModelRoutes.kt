package com.rentmycar.routing

import com.rentmycar.controllers.ModelController
import io.ktor.server.auth.*
import io.ktor.server.routing.*

fun Route.modelRoutes() {
    val modelController = ModelController()
    val prefix = "/model"

    authenticate {
        get("$prefix/all") { modelController.getAllModels(call) }
        get("$prefix/brand/{id}") { modelController.getModelsByBrand(call) }
    }
}
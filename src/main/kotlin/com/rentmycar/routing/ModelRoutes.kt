package com.rentmycar.routing

import com.rentmycar.controllers.ModelController
import io.ktor.server.auth.*
import io.ktor.server.routing.*

fun Route.modelRoutes() {
    val modelController = ModelController()

    authenticate {
        route("/model") {
            get("/all") { modelController.getAllModels(call) }
            get("/brand/{id}") { modelController.getModelsByBrand(call) }
        }
    }
}
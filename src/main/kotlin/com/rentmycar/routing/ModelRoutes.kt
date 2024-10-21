package com.rentmycar.routing

import com.rentmycar.routing.controllers.ModelController
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

    authenticate("admin") {
        route("/model") {
            post("/") { modelController.createModel(call) }
            put("/") { modelController.updateModel(call) }
            delete("/{id}") { modelController.deleteModel(call) }
        }
    }
}
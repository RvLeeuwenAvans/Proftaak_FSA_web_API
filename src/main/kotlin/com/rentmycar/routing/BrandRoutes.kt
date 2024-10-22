package com.rentmycar.routing

import com.rentmycar.controllers.BrandController
import io.ktor.server.auth.*
import io.ktor.server.routing.*

fun Route.brandRoutes() {
    val brandController = BrandController()

    authenticate {
        route("/brand") {
            get("/all") { brandController.getAllBrands(call) }
        }
    }

    authenticate("admin") {
        route("/brand") {
            post("/") { brandController.createBrand(call) }
            put("/") { brandController.updateBrand(call) }
            delete("/{id}") { brandController.deleteBrand(call) }
        }
    }
}
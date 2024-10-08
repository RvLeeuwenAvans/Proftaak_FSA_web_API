package com.rentmycar.routing

import com.rentmycar.controllers.BrandController
import io.ktor.server.auth.*
import io.ktor.server.http.content.*
import io.ktor.server.routing.*

fun Route.brandRoutes() {
    val brandController = BrandController()

    authenticate {
        route("/brand") {
            get("/all") { brandController.getAllBrands(call) }
        }
    }
}
package com.rentmycar.routing

import com.rentmycar.controllers.BrandController
import io.ktor.server.auth.*
import io.ktor.server.routing.*

fun Route.brandRoutes() {
    val brandController = BrandController()
    val prefix = "/brand"

    authenticate {
        get("$prefix/all") { brandController.getAllBrands(call) }
    }
}
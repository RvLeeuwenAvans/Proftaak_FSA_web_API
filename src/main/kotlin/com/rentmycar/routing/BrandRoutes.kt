package com.rentmycar.routing

import com.rentmycar.modules.cars.brands.BrandService
import io.ktor.server.auth.*
import io.ktor.server.routing.*

fun Route.brandRoutes() {
    val brandController = BrandService()
    val prefix = "/brand"

    authenticate {
        get("$prefix/all") { brandController.getAllBrands(call) }
    }
}
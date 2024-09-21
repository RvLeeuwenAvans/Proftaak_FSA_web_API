package com.rentmycar.routing

import com.rentmycar.authentication.jwtConfig
import com.rentmycar.controllers.UserController
import io.ktor.server.routing.*

fun Route.userRoutes() {
    val userController = UserController(jwtConfig(environment.config))

    post("/register") { userController.register(call) }
    post("/login") { userController.login(call) }
}
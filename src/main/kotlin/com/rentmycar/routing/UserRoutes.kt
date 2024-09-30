package com.rentmycar.routing

import com.rentmycar.authentication.jwtConfig
import com.rentmycar.controllers.UserController
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.userRoutes() {
    val userController = UserController(jwtConfig(environment.config))

    post("/user/register") { userController.registerUser(call) }
    post("/user/login") { userController.login(call) }

    authenticate {
        get("/test") { call.respondText("This is a protected route") }
    }
}
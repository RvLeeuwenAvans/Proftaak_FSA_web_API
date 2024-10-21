package com.rentmycar.routing

import com.rentmycar.authentication.jwtConfig
import com.rentmycar.routing.controllers.UserController
import io.ktor.server.auth.*
import io.ktor.server.routing.*

fun Route.userRoutes() {
    val userController = UserController(jwtConfig(environment.config))

    route("/user") {
        post("/register") { userController.registerUser(call) }
        post("/login") { userController.loginUser(call) }
        
        authenticate {
            post("/update") { userController.updateUser(call) }
            delete("/delete") { userController.deleteUser(call) }
        }
    }
}
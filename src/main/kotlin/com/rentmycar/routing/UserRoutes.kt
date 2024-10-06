package com.rentmycar.routing

import com.rentmycar.authentication.jwtConfig
import com.rentmycar.modules.users.UserService
import io.ktor.server.auth.*
import io.ktor.server.routing.*

fun Route.userRoutes() {
    val userService = UserService(jwtConfig(environment.config))

    post("/user/register") { userService.registerUser(call) }
    post("/user/login") { userService.loginUser(call) }

    authenticate {
        post("/user/update") { userService.updateUser(call) }
    }
}
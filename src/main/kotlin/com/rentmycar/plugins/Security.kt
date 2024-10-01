package com.rentmycar.plugins

import com.rentmycar.authentication.jwtConfig
import com.rentmycar.entities.User
import com.rentmycar.repositories.UserRepository
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*

fun Application.configureSecurity() {
    val applicationConfig = environment.config
    val userRepository = UserRepository()

    authentication {
        jwt {
            val config = jwtConfig(applicationConfig)
            realm = config.realm
            verifier(config.verifier())
            validate { credential ->
                if (userRepository.doesUserExistByEmail(credential.payload.getClaim("email").asString()))
                    JWTPrincipal(credential.payload)
                else null
            }
        }
    }
}

fun ApplicationCall.user(): User {
    val userId = this.principal<JWTPrincipal>()?.getClaim("id", Int::class)
        ?: throw IllegalArgumentException("User not authenticated")

    return UserRepository().getUserById(userId)
}

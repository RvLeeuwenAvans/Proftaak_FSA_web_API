package com.rentmycar.plugins

import com.rentmycar.authentication.jwtConfig
import com.rentmycar.entities.User
import com.rentmycar.repositories.UserRepository
import com.rentmycar.utils.UserRole
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import javax.naming.AuthenticationException

fun Application.configureSecurity() {
    val applicationConfig = environment.config
    val userRepository = UserRepository()

    authentication {
        val config = jwtConfig(applicationConfig)

        jwt {
            realm = config.realm
            verifier(config.verifier())
            validate { credential ->
                if (userRepository.doesUserExistByEmail(credential.payload.getClaim("email").asString()))
                    JWTPrincipal(credential.payload)
                else null
            }
        }

        jwt("admin") {
            realm = config.realm
            verifier(config.verifier())
            validate { credential ->
                if (
                    userRepository.doesUserExistByEmail(credential.payload.getClaim("email").asString()) &&
                    userRepository.getUserRole(credential.payload.getClaim("id").asInt()) == UserRole.ADMIN
                )
                    JWTPrincipal(credential.payload)
                else null
            }
        }
    }
}

fun ApplicationCall.user(): User {
    val userId = this.principal<JWTPrincipal>()?.getClaim("id", Int::class)
        ?: throw AuthenticationException("User not authenticated")

    return UserRepository().getUserById(userId)
}

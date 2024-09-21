package com.rentmycar.plugins

import com.rentmycar.authentication.jwtConfig
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*

fun Application.configureSecurity() {
    val applicationConfig = environment.config


    authentication {
        jwt {
            val config = jwtConfig(applicationConfig)
            realm = config.realm
            verifier(config.verifier())
            validate { credential ->
                if (credential.payload.getClaim("email").asString()
                        .isNotEmpty()
                ) JWTPrincipal(credential.payload) else null
            }
        }
    }
}
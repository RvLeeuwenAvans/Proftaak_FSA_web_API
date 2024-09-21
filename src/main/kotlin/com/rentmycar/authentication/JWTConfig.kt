package com.rentmycar.authentication

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.server.config.*

data class JWTConfig(val secret: String, val issuer: String, val audience: String, val realm: String) {
    val algorithm: Algorithm = Algorithm.HMAC256(secret)

    fun verifier(): JWTVerifier = JWT
        .require(algorithm)
        .withAudience(audience)
        .withIssuer(issuer)
        .build()
}

fun jwtConfig(config: ApplicationConfig): JWTConfig {
    val secret = config.property("ktor.security.jwt.secret").getString()
    val issuer = config.property("ktor.security.jwt.issuer").getString()
    val audience = config.property("ktor.security.jwt.audience").getString()
    val realm = config.property("ktor.security.jwt.realm").getString()

    return JWTConfig(secret, issuer, audience, realm)
}

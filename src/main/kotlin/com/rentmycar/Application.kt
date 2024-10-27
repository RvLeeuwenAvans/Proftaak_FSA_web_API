package com.rentmycar

import com.rentmycar.plugins.configureSecurity
import com.rentmycar.plugins.*
import io.ktor.server.application.*
import io.ktor.server.netty.*

fun main(args: Array<String>): Unit = EngineMain.main(args)

fun Application.module() {
    val environmentType = environment.config.property("ktor.environment.type").getString()

    configureSecurity()
    configureSerialization()
    when (environmentType) {
        "test" -> configureDatabases(seed = false)
        else -> configureDatabases()
    }
    configureErrorHandling()
    configureRouting()
}
package com.rentmycar

import com.rentmycar.plugins.configureSecurity
import com.rentmycar.plugins.*
import io.ktor.server.application.*
import io.ktor.server.netty.*

fun main(args: Array<String>): Unit = EngineMain.main(args)

fun Application.module() {
    configureSecurity()
    configureSerialization()
    configureDatabases()
    configureErrorHandling()
    configureRouting()
}

package com.rentmycar

import com.rentmycar.plugins.configureSecurity
import com.rentmycar.plugins.*
import com.rentmycar.routing.notificationRoutes
import io.ktor.server.application.*
import io.ktor.server.netty.*
import io.ktor.server.routing.* // Import for routing
fun main(args: Array<String>): Unit = EngineMain.main(args)

fun Application.module() {
    configureSecurity()
    configureSerialization()
    configureDatabases()
    configureErrorHandling()
    configureRouting()

}
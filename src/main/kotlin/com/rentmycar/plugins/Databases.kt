package com.rentmycar.plugins

import com.rentmycar.entities.Users
import io.ktor.server.application.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

fun Application.configureDatabases() {
    val config = environment.config
    val dbUrl = config.property("ktor.database.url").getString()
    val dbDriver = config.property("ktor.database.driver").getString()
    val dbUser = config.property("ktor.database.user").getString()
    val dbPassword = config.property("ktor.database.password").getString()

    Database.connect(dbUrl, dbDriver, dbUser, dbPassword)

    transaction {
        SchemaUtils.create(Users)
    }
}

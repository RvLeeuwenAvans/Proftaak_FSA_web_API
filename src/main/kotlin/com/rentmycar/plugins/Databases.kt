package com.rentmycar.plugins

import com.rentmycar.entities.*
import com.rentmycar.entities.seeders.Seeder
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

    // Connecting to the database
    Database.connect(dbUrl, dbDriver, dbUser, dbPassword)

    // Creating tables based on the ERD
    transaction {
        SchemaUtils.create(Users)         // Users table
        SchemaUtils.create(Cars)          // Cars table
        SchemaUtils.create(Models)        // Models table
        SchemaUtils.create(Brands)        // Brands table
        SchemaUtils.create(Timeslots)     // Timeslots table
        SchemaUtils.create(Locations)     // Locations table
        SchemaUtils.create(Images)        // Images table
        SchemaUtils.create(Reservations)  // Reservations table
        SchemaUtils.create(Notifications) // Notifications table

        // Seed tables with default values for testing purposes.
        if (environment.config.propertyOrNull("ktor.environment.type")?.getString() == "development") {
            Seeder()
        }
    }
}

package com.rentmycar.plugins

import com.rentmycar.modules.availability.Timeslots
import com.rentmycar.modules.cars.Cars
import com.rentmycar.modules.cars.brands.Brands
import com.rentmycar.modules.cars.brands.models.Models
import com.rentmycar.modules.cars.fuels.Fuels
import com.rentmycar.modules.cars.images.Images
import com.rentmycar.modules.cars.locations.Locations
import com.rentmycar.modules.cars.seedBrandsAndModels
import com.rentmycar.modules.cars.seedFuels
import com.rentmycar.modules.users.Users
import com.rentmycar.modules.users.reservations.Reservations
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
        SchemaUtils.create(Fuels)         // Fuels table
        SchemaUtils.create(Brands)        // Brands table
        SchemaUtils.create(Timeslots)     // Timeslots table
        SchemaUtils.create(Locations)     // Locations table
        SchemaUtils.create(Images)        // Images table
        SchemaUtils.create(Reservations)  // Reservations table

        // Seed tables with default values for testing purposes.
        seedFuels()
        seedBrandsAndModels()
    }
}

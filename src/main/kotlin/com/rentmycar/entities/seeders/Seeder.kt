package com.rentmycar.entities.seeders

import com.rentmycar.authentication.PasswordHasher
import com.rentmycar.entities.Brands
import com.rentmycar.entities.Brands.name
import com.rentmycar.entities.Car
import com.rentmycar.entities.Cars
import com.rentmycar.entities.Location
import com.rentmycar.entities.Locations
import com.rentmycar.entities.Locations.car
import com.rentmycar.entities.Models
import com.rentmycar.entities.Reservations
import com.rentmycar.entities.Reservations.timeslot
import com.rentmycar.entities.Timeslots
import com.rentmycar.entities.Users
import com.rentmycar.entities.Users.username
import com.rentmycar.utils.FuelType
import com.rentmycar.utils.Transmission
import com.rentmycar.utils.UserRole
import com.rentmycar.utils.*
import kotlinx.datetime.toJavaLocalDateTime
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.*
import kotlin.collections.set

class Seeder {
    private val brandsMap = mutableMapOf<String, Int>()
    private val modelIDs = mutableListOf<Int>()
    private val userIDs = mutableListOf<Int>()
    private val carIDs = mutableListOf<Int>()
    private val timeslotIDs = mutableListOf<Int>()


    /**
     * Seed the brands table and fill the brandsMap with brandName-ID entries.
     */
    private fun seedBrands() = transaction {
        for (brand in brands) {
            val exists = Brands.selectAll().where { name eq brand }.singleOrNull()

            val brandId = if (exists == null) {
                Brands.insertAndGetId { it[name] = brand }.value
            } else {
                exists[Brands.id].value
            }

            brandsMap[brand] = brandId
        }
    }

    /**
     * Seed the Models table and get the IDs of available models.
     */
    private fun seedModels() = transaction {
        for ((modelName, brandName) in models) {
            val brandId = brandsMap[brandName]
            val exists = Models.select(Models.id).where { Models.name eq modelName }.singleOrNull()

            if (exists == null) {
                val modelID = Models.insertAndGetId {
                    it[name] = modelName
                    it[brand] = brandId!!
                }

                modelIDs.add(modelID.value)
            }
        }
    }

    /**
     * Seed the Users table and get the IDs of the available users.
     */
    private fun seedUsers() = transaction {
        for ((index, user) in users.withIndex()) {
            val exists = Users.select(Users.id).where { username eq user.username }.singleOrNull()

            if (exists == null) {
                val userID = Users.insertAndGetId {
                    it[id] = index + 1
                    it[firstName] = user.firstName
                    it[lastName] = user.lastName
                    it[username] = user.username
                    it[email] = user.email
                    it[password] = PasswordHasher.hashPassword(user.password)
                    it[role] = if (user.email.endsWith("@student.avans.nl")) UserRole.ADMIN else UserRole.DEFAULT
                }

                userIDs.add(userID.value)
            }
        }
    }

    /**
     * Seed the Cars table and get the IDs of the available cars.
     */
    private fun seedCars() = transaction {
        for ((index, car) in cars.withIndex()) {
            val randomUserID = if (userIDs.isNotEmpty()) userIDs.random() else -1
            val randomModelID = if (modelIDs.isNotEmpty()) modelIDs.random() else -1

            val carExists = Cars.select(Cars.id).where { Cars.licensePlate eq car.licensePlate }.singleOrNull()

            if (carExists == null && randomModelID != -1 && randomUserID != -1) {
                val carID = Cars.insertAndGetId {
                    it[id] = index + 1
                    it[user] = randomUserID
                    it[model] = randomModelID
                    it[location] = null
                    it[licensePlate] = car.licensePlate
                    it[year] = car.year
                    it[color] = car.color
                    it[price] = car.price
                    it[transmission] = Transmission.valueOf(car.transmission.uppercase())
                    it[fuel] = FuelType.valueOf(car.fuel.uppercase())
                    it[category] = FuelType.valueOf(car.fuel.uppercase()).category
                }

                carIDs.add(carID.value)
            }
        }
    }

    /**
     * Seed the Locations table and update the Cars table with the references to the locations.
     */
    private fun seedLocations() = transaction {
        for ((index, location) in locations.withIndex()) {
            val foundCar = Cars.select(Cars.id).where { Cars.id eq location.carId }.singleOrNull()
            val carId = if (foundCar != null) location.carId else carIDs.random()
            val locationExists = Locations.select(Locations.id).where { car eq location.carId }.singleOrNull()

            if (locationExists == null) {
                val createdLocationId = Locations.insertAndGetId {
                    it[id] = index + 1
                    it[car] = carId
                    it[longitude] = location.longitude
                    it[latitude] = location.latitude
                }

                val referencedCar = Car.find { Cars.id eq carId }.singleOrNull()
                referencedCar?.apply { this.location = Location.findById(createdLocationId) }
            }
        }
    }

    /**
     * Seed the Timeslots table and get the IDs of the available timeslots.
     */
    private fun seedTimeslots() = transaction {
        val timeslotsAmount = Timeslots.select(Timeslots.id).count()
        if (timeslotsAmount.toInt() != 0) return@transaction

        for ((index, timeslot) in timeslots.withIndex()) {
            val carExists = Cars.select(Cars.id).where { Cars.id eq timeslot.carId }.singleOrNull()

            if (timeslotsAmount < timeslots.size) {
                val timeslotID = Timeslots.insertAndGetId {
                    it[id] = index + 1
                    it[car] = if (carExists != null) timeslot.carId else carIDs.random()
                    it[availableFrom] = timeslot.availableFrom.toJavaLocalDateTime()
                    it[availableUntil] = timeslot.availableUntil.toJavaLocalDateTime()
                }

                timeslotIDs.add(timeslotID.value)
            }
        }
    }

    /**
     * Seed the Reservations table.
     */
    private fun seedReservations() = transaction {
        for ((index, reservation) in reservations.withIndex()) {
            val timeslotExists = Timeslots.select(Timeslots.id).where { Timeslots.id eq reservation.timeslotId }.singleOrNull()
            val reservationExists = Reservations.select(Reservations.id).where { timeslot eq reservation.timeslotId }.singleOrNull()

            if (reservationExists == null) {
                Reservations.insert {
                    it[id] = index + 1
                    it[timeslot] =
                        if (timeslotExists != null) reservation.timeslotId else timeslotIDs.random()
                    it[user] = userIDs.random()
                }
            }
        }
    }

    init {
        transaction {
            seedBrands()
            seedModels()
            seedUsers()
            seedCars()
            seedLocations()
            seedTimeslots()
            seedReservations()
        }
    }
}
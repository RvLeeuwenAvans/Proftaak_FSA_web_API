package com.rentmycar.car

import com.rentmycar.BaseTest
import com.rentmycar.dtos.requests.location.LocationRequest
import com.rentmycar.dtos.requests.timeslot.CreateTimeSlotRequest
import com.rentmycar.dtos.requests.user.UserRegistrationRequest
import com.rentmycar.entities.Cars
import com.rentmycar.entities.Locations
import com.rentmycar.entities.Timeslots
import com.rentmycar.entities.Users
import com.rentmycar.utils.Category
import com.rentmycar.utils.FuelType
import com.rentmycar.utils.Transmission
import kotlinx.datetime.toJavaLocalDateTime
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.test.AfterTest
import kotlin.test.BeforeTest

data class CarSeedData(
    val userId: Int,
    val licensePlate: String,
    val modelId: Int,
    val fuel: FuelType,
    val category: Category,
    val year: Int,
    val color: String,
    val transmission: Transmission,
    val price: Double,
)

open class CarTestBase(
    usersSeedData: MutableList<UserRegistrationRequest> = mutableListOf(),
    private val carsSeedData: MutableList<CarSeedData> = mutableListOf(),
    private val locationSeedData: MutableList<LocationRequest> = mutableListOf(),
    private val timeslotSeedData: MutableList<CreateTimeSlotRequest> = mutableListOf(),
): BaseTest(usersSeedData) {
    @BeforeTest
    fun seedTable() {
        transaction {
            for ((index, car) in carsSeedData.withIndex()) {
                Cars.insert {
                    it[id] = index + 1
                    it[user] = car.userId
                    it[licensePlate] = car.licensePlate
                    it[model] = car.modelId
                    it[fuel] = car.fuel
                    it[category] = car.category
                    it[year] = car.year
                    it[color] = car.color
                    it[transmission] = car.transmission
                    it[price] = car.price
                }
            }

            for (location in locationSeedData) {
                Locations.insert {
                    it[car] = location.carId
                    it[longitude] = location.longitude
                    it[latitude] = location.latitude
                }
            }

            for (timeslot in timeslotSeedData) {
                Timeslots.insert {
                    it[car] = timeslot.carId
                    it[availableFrom] = timeslot.availableFrom.toJavaLocalDateTime()
                    it[availableUntil] = timeslot.availableUntil.toJavaLocalDateTime()
                }
            }
        }
    }

    @AfterTest
    fun clearDb () {
        transaction {
            Users.deleteAll()
            Cars.deleteAll()
        }
    }
}
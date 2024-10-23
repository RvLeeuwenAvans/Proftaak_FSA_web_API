package com.rentmycar.car

import com.rentmycar.BaseTest
import com.rentmycar.authentication.PasswordHasher
import com.rentmycar.dtos.requests.location.LocationRequest
import com.rentmycar.dtos.requests.timeslot.CreateTimeSlotRequest
import com.rentmycar.dtos.requests.user.UserLoginRequest
import com.rentmycar.dtos.requests.user.UserRegistrationRequest
import com.rentmycar.entities.Cars
import com.rentmycar.entities.Locations
import com.rentmycar.entities.Timeslots
import com.rentmycar.entities.Users
import com.rentmycar.utils.Category
import com.rentmycar.utils.FuelType
import com.rentmycar.utils.Transmission
import com.rentmycar.utils.UserRole
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.serialization.json.Json
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
    private val usersSeedData: MutableList<UserRegistrationRequest> = mutableListOf(),
    private val carsSeedData: MutableList<CarSeedData> = mutableListOf(),
    private val locationSeedData: MutableList<LocationRequest> = mutableListOf(),
    private val timeslotSeedData: MutableList<CreateTimeSlotRequest> = mutableListOf(),
): BaseTest() {
    @BeforeTest
    fun seedTable() {
        transaction {
            for ((index, user) in usersSeedData.withIndex()) {
                Users.insert {
                    it[id] = index + 1
                    it[firstName] = user.firstName
                    it[lastName] = user.lastName
                    it[username] = user.username
                    it[email] = user.email
                    it[password] = PasswordHasher.hashPassword(user.password)
                    it[role] = UserRole.ADMIN
                }
            }
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

    protected suspend fun getToken(client: HttpClient, email: String = "testuser4444@gmail.com"): String {
        val response = client.post("/user/login") {
            contentType(ContentType.Application.Json)
            setBody(Json.encodeToString(
                UserLoginRequest.serializer(), UserLoginRequest(
                email = email,
                password = "fakepwd"
            )))
        }

        val data = Json.decodeFromString<Map<String, String>>(response.bodyAsText())
        return data["token"] ?: throw Exception("No token")
    }

    @AfterTest
    fun clearDb () {
        transaction {
            Users.deleteAll()
            Cars.deleteAll()
        }
    }
}
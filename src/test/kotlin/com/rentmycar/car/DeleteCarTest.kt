package com.rentmycar.car

import com.rentmycar.requests.location.LocationRequest
import com.rentmycar.requests.timeslot.CreateTimeSlotRequest
import com.rentmycar.requests.user.UserRegistrationRequest
import com.rentmycar.utils.Category
import com.rentmycar.utils.FuelType
import com.rentmycar.utils.Transmission
import io.ktor.client.HttpClient
import io.ktor.client.request.delete
import io.ktor.client.request.headers
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlinx.datetime.toLocalDateTime
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals

val usersSeedDataDelete = mutableListOf<UserRegistrationRequest>(
    UserRegistrationRequest(firstName = "Test", lastName = "User", username = "testuser4444", email = "testuser4444@gmail.com", password = "fakepwd"),
    UserRegistrationRequest(firstName = "Test 2", lastName = "User 2", username = "testuser2", email = "testuser2@gmail.com", password = "fakepwd"),
)

val carsSeedDataDelete = mutableListOf<CarSeedData>(
    CarSeedData(userId = 1, licensePlate = Random.nextInt().toString(16), modelId = 1, fuel = FuelType.DIESEL, category = Category.ICE, year = 2004, color = "Pink", transmission = Transmission.AUTOMATIC, price = 4444.0),
    CarSeedData(userId = 1, licensePlate = Random.nextInt().toString(16), modelId = 1, fuel = FuelType.DIESEL, category = Category.ICE, year = 2004, color = "Pink", transmission = Transmission.AUTOMATIC, price = 4444.0),
    CarSeedData(userId = 1, licensePlate = Random.nextInt().toString(16), modelId = 1, fuel = FuelType.DIESEL, category = Category.ICE, year = 2004, color = "Pink", transmission = Transmission.AUTOMATIC, price = 4444.0),
)

val timeslotSeedDataDelete = mutableListOf<CreateTimeSlotRequest>(
    CreateTimeSlotRequest(
        carId = 2,
        availableFrom = "2024-10-20T09:00".toLocalDateTime(),
        availableUntil = "2024-10-20T17:00".toLocalDateTime()
    )
)

val locationSeedDataDelete = mutableListOf<LocationRequest>(
    LocationRequest(carId = 3, longitude = 40.0, latitude = -40.0)
)

class DeleteCarTest: CarTestBase(
    usersSeedDataDelete,
    carsSeedDataDelete,
    locationSeedDataDelete,
    timeslotSeedDataDelete,
) {
    private suspend fun deleteCar(
        client: HttpClient,
        id: Int,
        email: String = "testuser4444@gmail.com"
    ): HttpResponse {
        val token = getToken(client, email)

        return client.delete("/car/delete/$id") {
            contentType(ContentType.Application.Json)
            headers {
                append(HttpHeaders.Authorization, "Bearer $token")
            }
        }
    }

    @Test
    fun successfulDeleteCar() = withTestApplication {
        val response = deleteCar(client, 1)
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("Car deleted successfully.", response.bodyAsText())
    }

    @Test
    fun userIsNotCarOwner() = withTestApplication {
        val response = deleteCar(client, 1, "testuser2@gmail.com")
        assertEquals(HttpStatusCode.Forbidden, response.status)
        assertEquals("{\"error\":\"User does not own car with id: 1\"}", response.bodyAsText())
    }

    @Test
    fun carHasLinkedTimeslots() = withTestApplication {
        val response = deleteCar(client, 2)
        assertEquals(HttpStatusCode.Forbidden, response.status)
        assertEquals("{\"error\":\"Car can not be deleted when it has linked timeslots\"}", response.bodyAsText())
    }

    @Test
    fun successfulWithLinkedLocation() = withTestApplication {
        val response = deleteCar(client, 3)
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("Car deleted successfully.", response.bodyAsText())
    }
}
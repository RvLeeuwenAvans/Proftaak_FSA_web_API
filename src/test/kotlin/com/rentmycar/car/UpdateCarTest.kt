package com.rentmycar.car

import com.rentmycar.entities.Cars
import com.rentmycar.requests.car.UpdateCarRequest
import com.rentmycar.requests.user.UserRegistrationRequest
import com.rentmycar.utils.Category
import com.rentmycar.utils.FuelType
import com.rentmycar.utils.Transmission
import io.ktor.client.HttpClient
import io.ktor.client.request.headers
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import junit.framework.TestCase.assertEquals
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.Test
import java.util.Calendar
import kotlin.random.Random
import kotlin.test.AfterTest

val usersSeedDataUpdate = mutableListOf<UserRegistrationRequest>(
    UserRegistrationRequest(firstName = "Test", lastName = "User", username = "testuser4444", email = "testuser4444@gmail.com", password = "fakepwd"),
    UserRegistrationRequest(firstName = "Test 2", lastName = "User 2", username = "testuser2", email = "testuser2@gmail.com", password = "fakepwd"),
)

val carsSeedDataUpdate = mutableListOf<CarSeedData>(
    CarSeedData(userId = 1, licensePlate = Random.nextInt().toString(16), modelId = 1, fuel = FuelType.DIESEL, category = Category.ICE, year = 2004, color = "Pink", transmission = Transmission.AUTOMATIC, price = 4444.0),
)

class UpdateCarTest: CarTestBase(usersSeedDataUpdate, carsSeedDataUpdate) {
    private val request1 = UpdateCarRequest(
        carId = 1,
        year = 2020,
        color = "Pink and Yellow",
        transmission = "manual",
        fuel = "hydrogen",
        price = 44.44,
    )
    private val request2 = UpdateCarRequest(
        carId = 1,
        price = 4444.0
    )
    private val request3 = UpdateCarRequest(
        carId = -1,
    )
    private val request4 = UpdateCarRequest(
        carId = 1,
        color = ""
    )
    private val request5 = UpdateCarRequest(
        carId = 1,
        year = 1444
    )
    private val request6 = UpdateCarRequest(
        carId = 1,
        year = 2100
    )
    private val request7 = UpdateCarRequest(
        carId = 1,
        fuel = "Invalid"
    )
    private val request8 = UpdateCarRequest(
        carId = 1,
        transmission = "Invalid"
    )
    private val request9 = UpdateCarRequest(
        carId = 1,
        price = -44.44
    )
    private val request10 = UpdateCarRequest(
        carId = 1,
        fuel = "Invalid",
        transmission = "Invalid"
    )
    private val request11 = UpdateCarRequest(
        carId = 44,
        color = "Pink and Yellow"
    )

    private suspend fun updateCar(
        client: HttpClient,
        request: UpdateCarRequest,
        email: String = "testuser4444@gmail.com"
    ): HttpResponse {
        val token = getToken(client, email)

        return client.put("/car/update") {
            contentType(ContentType.Application.Json)
            setBody(Json.encodeToString(UpdateCarRequest.serializer(), request))
            headers {
                append(HttpHeaders.Authorization, "Bearer $token")
            }
        }
    }

    @Test
    fun successfulRequestUpdateAll() = withTestApplication {
        val response = updateCar(client, request1)
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("Car updated successfully", response.bodyAsText())
    }

    @Test
    fun successfulRequestUpdatePartially() = withTestApplication {
        val response = updateCar(client, request2)
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("Car updated successfully", response.bodyAsText())
    }

    @Test
    fun idNegative() = withTestApplication {
        val response = updateCar(client, request3)
        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertEquals("{\"errors\":[\"Car ID is invalid\"]}", response.bodyAsText())
    }

    @Test
    fun colorBlank() = withTestApplication {
        val response = updateCar(client, request4)
        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertEquals("{\"errors\":[\"Color cannot be blank\"]}", response.bodyAsText())
    }

    @Test
    fun yearLessThanAllowed() = withTestApplication {
        val currentYear = Calendar.getInstance().get(Calendar.YEAR).toInt()
        val response = updateCar(client, request5)
        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertEquals("{\"errors\":[\"Year must be between 1886 and $currentYear\"]}", response.bodyAsText())
    }

    @Test
    fun yearMoreThanAllowed() = withTestApplication {
        val currentYear = Calendar.getInstance().get(Calendar.YEAR).toInt()
        val response = updateCar(client, request6)
        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertEquals("{\"errors\":[\"Year must be between 1886 and $currentYear\"]}", response.bodyAsText())
    }

    @Test
    fun fuelTypeInvalid() = withTestApplication {
        val response = updateCar(client, request7)
        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertEquals("{\"errors\":[\"Fuel is invalid\"]}", response.bodyAsText())
    }

    @Test
    fun transmissionInvalid() = withTestApplication {
        val response = updateCar(client, request8)
        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertEquals("{\"errors\":[\"Transmission is invalid\"]}", response.bodyAsText())
    }

    @Test
    fun priceNegative() = withTestApplication {
        val response = updateCar(client, request9)
        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertEquals("{\"errors\":[\"Price must be a non-negative number\"]}", response.bodyAsText())
    }

    @Test
    fun validationErrorsMixed() = withTestApplication {
        val response = updateCar(client, request10)
        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertEquals("{\"errors\":[\"Fuel is invalid\",\"Transmission is invalid\"]}", response.bodyAsText())
    }

    @Test
    fun carNotFound() = withTestApplication {
        val response = updateCar(client, request11)
        assertEquals(HttpStatusCode.NotFound, response.status)
        assertEquals("{\"error\":\"Car with id 44 not found\"}", response.bodyAsText())
    }

    @Test
    fun userNotCarOwner() = withTestApplication {
        val response = updateCar(client, request1, "testuser2@gmail.com")
        assertEquals(HttpStatusCode.Forbidden, response.status)
        assertEquals("{\"error\":\"User does not own car with id: 1\"}", response.bodyAsText())
    }

    @AfterTest
    fun clearCars() {
        transaction {
            Cars.deleteAll()
        }
    }
}
package com.rentmycar.car

import com.rentmycar.requests.location.LocationRequest
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
import kotlinx.serialization.json.Json
import org.junit.Test
import kotlin.random.Random
import kotlin.test.assertEquals

val usersSeedDataUpdateLocation = mutableListOf<UserRegistrationRequest>(
    UserRegistrationRequest(firstName = "Test", lastName = "User", username = "testuser4444", email = "testuser4444@gmail.com", password = "fakepwd"),
    UserRegistrationRequest(firstName = "Test 2", lastName = "User 2", username = "testuser2", email = "testuser2@gmail.com", password = "fakepwd"),
)

val carsSeedDataUpdateLocation = mutableListOf<CarSeedData>(
    CarSeedData(userId = 1, licensePlate = Random.nextInt().toString(16), modelId = 1, fuel = FuelType.DIESEL, category = Category.ICE, year = 2004, color = "Pink", transmission = Transmission.AUTOMATIC, price = 4444.0),
    CarSeedData(userId = 1, licensePlate = Random.nextInt().toString(16), modelId = 1, fuel = FuelType.DIESEL, category = Category.ICE, year = 2004, color = "Pink", transmission = Transmission.AUTOMATIC, price = 4444.0),
)

val locationSeedDataUpdateLocation = mutableListOf<LocationRequest>(
    LocationRequest(carId = 1, longitude = 40.0, latitude = 40.0)
)

class UpdateLocationTest: CarTestBase(
    usersSeedDataUpdateLocation,
    carsSeedDataUpdateLocation,
    locationSeedDataUpdateLocation,
) {
    private suspend fun updateLocation(
        client: HttpClient,
        request: LocationRequest,
        email: String = "testuser4444@gmail.com"
    ): HttpResponse {
        val token = getToken(client, email)

        return client.put("/car/location/") {
            contentType(ContentType.Application.Json)
            setBody(Json.encodeToString(LocationRequest.serializer(), request))
            headers {
                append(HttpHeaders.Authorization, "Bearer $token")
            }
        }
    }

    private val request1 = LocationRequest(1, 40.0, 40.0)
    private val request2 = request1.copy(carId = -1)
    private val request3 = request1.copy(longitude = -100.0)
    private val request4 = request1.copy(latitude = 100.0)
    private val request5 = request1.copy(carId = 100)
    private val request6 = request1.copy(carId = 2)

    @Test
    fun successfulUpdateLocation() = withTestApplication {
        val response = updateLocation(client, request1)
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("Location updated successfully.", response.bodyAsText())
    }

    @Test
    fun userIsNotCarOwner() = withTestApplication {
        val response = updateLocation(client, request1, "testuser2@gmail.com")
        assertEquals(HttpStatusCode.Forbidden, response.status)
        assertEquals("{\"error\":\"User does not own car with id: 1\"}", response.bodyAsText())
    }

    @Test
    fun carIdInvalid() = withTestApplication {
        val response = updateLocation(client, request2)
        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertEquals("{\"errors\":[\"Car ID is invalid\"]}", response.bodyAsText())
    }

    @Test
    fun longitudeInvalid() = withTestApplication {
        val response = updateLocation(client, request3)
        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertEquals("{\"errors\":[\"Longitude is invalid\"]}", response.bodyAsText())
    }

    @Test
    fun latitudeInvalid() = withTestApplication {
        val response = updateLocation(client, request4)
        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertEquals("{\"errors\":[\"Latitude is invalid\"]}", response.bodyAsText())
    }

    @Test
    fun carNotExists() = withTestApplication {
        val response = updateLocation(client, request5)
        assertEquals(HttpStatusCode.NotFound, response.status)
        assertEquals("{\"error\":\"Car with id 100 not found\"}", response.bodyAsText())
    }

    @Test
    fun carHasNoLinkedLocation() = withTestApplication {
        val response = updateLocation(client, request6)
        assertEquals(HttpStatusCode.NotFound, response.status)
        assertEquals("{\"error\":\"Car location not found\"}", response.bodyAsText())
    }
}
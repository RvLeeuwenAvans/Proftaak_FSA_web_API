package com.rentmycar.car

import com.rentmycar.dtos.CarDTO
import com.rentmycar.dtos.requests.location.LocationRequest
import com.rentmycar.dtos.requests.user.UserRegistrationRequest
import com.rentmycar.utils.Category
import com.rentmycar.utils.FuelType
import com.rentmycar.utils.Transmission
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.json.Json
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

data class FilterResponse(val status: HttpStatusCode, val body: List<CarDTO>)

val usersSeedDataFilter = mutableListOf<UserRegistrationRequest>(
    UserRegistrationRequest(firstName = "Test", lastName = "User", username = "testuser4444", email = "testuser4444@gmail.com", password = "fakepwd"),
    UserRegistrationRequest(firstName = "Test 2", lastName = "User 2", username = "testuser2", email = "testuser2@gmail.com", password = "fakepwd"),
)

val carsSeedDataFilter = mutableListOf<CarSeedData>(
    CarSeedData(userId = 1, licensePlate = Random.nextInt().toString(16), modelId = 1, fuel = FuelType.DIESEL, category = Category.ICE, year = 2004, color = "Pink", transmission = Transmission.AUTOMATIC, price = 4444.0),
    CarSeedData(userId = 1, licensePlate = Random.nextInt().toString(16), modelId = 1, fuel = FuelType.ELECTRIC, category = Category.BEV, year = 2004, color = "Pink", transmission = Transmission.AUTOMATIC, price = 444.0),
    CarSeedData(userId = 1, licensePlate = Random.nextInt().toString(16), modelId = 1, fuel = FuelType.HYDROGEN, category = Category.FCEV, year = 2004, color = "Pink", transmission = Transmission.AUTOMATIC, price = 666.0),
    CarSeedData(userId = 1, licensePlate = Random.nextInt().toString(16), modelId = 1, fuel = FuelType.GAS, category = Category.ICE, year = 2004, color = "Pink", transmission = Transmission.AUTOMATIC, price = 100.0),
    CarSeedData(userId = 2, licensePlate = Random.nextInt().toString(16), modelId = 1, fuel = FuelType.PETROL, category = Category.ICE, year = 2004, color = "Pink", transmission = Transmission.AUTOMATIC, price = 666.0),
    CarSeedData(userId = 2, licensePlate = Random.nextInt().toString(16), modelId = 1, fuel = FuelType.DIESEL, category = Category.ICE, year = 2004, color = "Pink", transmission = Transmission.AUTOMATIC, price = 4444.0),
)

val locationSeedDataFilter = mutableListOf<LocationRequest>(
    LocationRequest(carId = 1, longitude = -0.116773, latitude = 51.510357)
)

class FilterCarsTest: CarTestBase(
    usersSeedDataFilter,
    carsSeedDataFilter,
    locationSeedDataFilter,
) {
    private suspend fun filterCars(
        client: HttpClient,
        filters: String
    ): FilterResponse {
        val token = getToken(client)

        val response = client.get("/car/all/filtered$filters") {
            contentType(ContentType.Application.Json)
            accept(ContentType.Application.Json)
            headers {
                append(HttpHeaders.Authorization, "Bearer $token")
            }
        }

        val body: List<CarDTO> = Json.decodeFromString(response.bodyAsText())

        return FilterResponse(response.status, body)
    }

    @Test
    fun filterByOwner() = withTestApplication {
        val response = filterCars(client, "?ownerId=2")
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(2, response.body.size)
        assertTrue(response.body.all { it.ownerId == 2 })
    }

    @Test
    fun filterByInvalidOwner() = withTestApplication {
        val response = filterCars(client, "?ownerId=abc")
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(6, response.body.size)
    }

    @Test
    fun filterByNonExistingOwner() = withTestApplication {
        val response = filterCars(client, "?ownerId=44")
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(0, response.body.size)
    }

    @Test
    fun filterByCategory() = withTestApplication {
        val response = filterCars(client, "?category=ice")
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(4, response.body.size)
        assertTrue(response.body.all { it.category == Category.ICE })
    }

    @Test
    fun filterByInvalidCategory() = withTestApplication {
        val response = filterCars(client, "?category=test")
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(6, response.body.size)
    }

    @Test
    fun filterByMinPrice() = withTestApplication {
        val response = filterCars(client, "?minPrice=600")
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(4, response.body.size)
        assertTrue(response.body.all { it.price >= 600 })
    }

    @Test
    fun filterByMinPriceButPriceIsInvalid() = withTestApplication {
        val response = filterCars(client, "?minPrice=abc")
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(6, response.body.size)
    }

    @Test
    fun filterByMaxPrice() = withTestApplication {
        val response = filterCars(client, "?maxPrice=600")
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(2, response.body.size)
        assertTrue(response.body.all { it.price <= 600 })
    }

    @Test
    fun filterByMaxPriceButPriceIsInvalid() = withTestApplication {
        val response = filterCars(client, "?maxPrice=abc")
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(6, response.body.size)
    }

    @Test
    fun filterByMinAndMaxPrice() = withTestApplication {
        val response = filterCars(client, "?minPrice=200&maxPrice=1000")
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(3, response.body.size)
        assertTrue(response.body.all { it.price in 200.0..1000.0 })
    }

    @Test
    fun filterByRadius() = withTestApplication {
        val response = filterCars(client, "?radius=5897660&longitude=-77.009003&latitude=38.889931")
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(1, response.body.size)
    }

    @Test
    fun filterByRadiusWithoutLongitude() = withTestApplication {
        val response = filterCars(client, "?radius=5897660&latitude=38.889931")
        assertEquals(HttpStatusCode.OK, response.status)
        // Does not apply the radius filter by default.
        assertEquals(6, response.body.size)
    }

    @Test
    fun filterByRadiusWithInvalidLatitude() = withTestApplication {
        val response = filterCars(client, "?radius=5897660&latitude=438.889931")
        assertEquals(HttpStatusCode.OK, response.status)
        // Does not apply the radius filter by default.
        assertEquals(6, response.body.size)
    }

    @Test
    fun filterByMixedParams() = withTestApplication {
        val response = filterCars(client, "?minPrice=0&maxPrice=1000&category=ice")
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(2, response.body.size)
    }
}
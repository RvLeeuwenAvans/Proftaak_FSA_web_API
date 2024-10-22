package com.rentmycar.car

import com.rentmycar.requests.car.RegisterCarRequest
import com.rentmycar.requests.user.UserRegistrationRequest
import io.ktor.client.HttpClient
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.*
import io.ktor.http.*
import junit.framework.TestCase.assertEquals
import kotlinx.serialization.json.Json
import java.util.Calendar
import kotlin.test.Test

val usersSeedDataRegister = mutableListOf<UserRegistrationRequest>(
    UserRegistrationRequest(firstName = "Test", lastName = "User", username = "testuser4444", email = "testuser4444@gmail.com", password = "fakepwd"),
)

class RegisterCarTest: CarTestBase(usersSeedDataRegister) {
    private val baseRequest = RegisterCarRequest(
        licensePlate = "CB1234BB",
        modelId = 1,
        fuel = "DIESEL",
        year = 2004,
        color = "Pink",
        transmission = "AUTOMATIC",
        price = 4444.0
    )

    private val request2 = baseRequest.copy(licensePlate = "")
    private val request3 = baseRequest.copy(modelId = -1)
    private val request4 = baseRequest.copy(fuel = "Invalid")
    private val request5 = baseRequest.copy(fuel = "diesel")
    private val request6 = baseRequest.copy(year = 1444)
    private val request7 = baseRequest.copy(year = 2100)
    private val request8 = baseRequest.copy(color = "")
    private val request9 = baseRequest.copy(transmission = "Invalid")
    private val request10 = baseRequest.copy(transmission = "automatic")
    private val request11 = baseRequest.copy(price = -44.44)
    private val request12 = baseRequest.copy(licensePlate = "", price = -44.44)

    private suspend fun registerCar(
        client: HttpClient,
        request: RegisterCarRequest
    ): HttpResponse {
        val token = getToken(client)

        return client.post("/car/register") {
            contentType(ContentType.Application.Json)
            setBody(Json.encodeToString(RegisterCarRequest.serializer(), request))
            headers {
                append(HttpHeaders.Authorization, "Bearer $token")
            }
        }
    }

    @Test
    fun successfulRequest() = withTestApplication {
        val response = registerCar(client, baseRequest)
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("Car registered successfully", response.bodyAsText())
    }

    @Test
    fun licensePlateBlank() = withTestApplication {
        val response = registerCar(client, request2)
        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertEquals("{\"errors\":[\"License plate cannot be blank\"]}", response.bodyAsText())
    }

    @Test
    fun modelIndexNegative() = withTestApplication {
        val response = registerCar(client, request3)
        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertEquals("{\"errors\":[\"Model ID is invalid\"]}", response.bodyAsText())
    }

    @Test
    fun fuelTypeInvalid() = withTestApplication {
        val response = registerCar(client, request4)
        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertEquals("{\"errors\":[\"Fuel is invalid\"]}", response.bodyAsText())
    }

    @Test
    fun successfulFuelTypeLowercase() = withTestApplication {
        val response = registerCar(client, request5)
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("Car registered successfully", response.bodyAsText())
    }

    @Test
    fun yearLessThanAllowed() = withTestApplication {
        val currentYear = Calendar.getInstance().get(Calendar.YEAR).toInt()
        val response = registerCar(client, request6)
        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertEquals("{\"errors\":[\"Year must be between 1886 and $currentYear\"]}", response.bodyAsText())
    }

    @Test
    fun yearMoreThanAllowed() = withTestApplication {
        val currentYear = Calendar.getInstance().get(Calendar.YEAR).toInt()
        val response = registerCar(client, request7)
        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertEquals("{\"errors\":[\"Year must be between 1886 and $currentYear\"]}", response.bodyAsText())
    }

    @Test
    fun colorBlank() = withTestApplication {
        val response = registerCar(client, request8)
        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertEquals("{\"errors\":[\"Color cannot be blank\"]}", response.bodyAsText())
    }

    @Test
    fun transmissionInvalid() = withTestApplication {
        val response = registerCar(client, request9)
        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertEquals("{\"errors\":[\"Transmission is invalid\"]}", response.bodyAsText())
    }

    @Test
    fun successfulTransmissionLowercase() = withTestApplication {
        val response = registerCar(client, request10)
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("Car registered successfully", response.bodyAsText())
    }

    @Test
    fun priceNegative() = withTestApplication {
        val response = registerCar(client, request11)
        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertEquals("{\"errors\":[\"Price must be a non-negative number\"]}", response.bodyAsText())
    }

    @Test
    fun validationErrorsMixed() = withTestApplication {
        val response = registerCar(client, request12)
        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertEquals("{\"errors\":[\"License plate cannot be blank\",\"Price must be a non-negative number\"]}", response.bodyAsText())
    }

    @Test
    fun licensePlateNonUnique() = withTestApplication {
        registerCar(client, baseRequest)

        val response = registerCar(client, baseRequest)
        assertEquals(HttpStatusCode.Conflict, response.status)
        assertEquals("{\"error\":\"Car with licensePlate: CB1234BB already exists\"}", response.bodyAsText())
    }
}
package com.rentmycar.acceleration

import com.rentmycar.BaseTest
import com.rentmycar.dtos.requests.user.UserRegistrationRequest
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlin.test.Test
import kotlin.test.assertEquals

val usersSeedDataAcc = mutableListOf<UserRegistrationRequest>(
    UserRegistrationRequest(firstName = "John", lastName = "Doe", username = "johndoe", email = "johndoe@example.com", password = "fakepwd"),
)

class AccelerationTest : BaseTest(usersSeedDataAcc) {
    @Test
    fun testCalculateVelocity() = withTestApplication {
        val token = getToken(client, "johndoe@example.com")
        val response = client.get("/acceleration/velocity?initialVelocity=10.0&acceleration=2.0&deltaTime=3.0") {
            contentType(ContentType.Application.Json)
            headers {
                append(HttpHeaders.Authorization, "Bearer $token")
            }
        }

        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("Velocity: 16.0", response.bodyAsText())
    }

    @Test
    fun testCalculateVelocityWithNegativeValues() = withTestApplication {
        val token = getToken(client, "johndoe@example.com")
        val response = client.get("/acceleration/velocity?initialVelocity=-10.0&acceleration=-2.0&deltaTime=3.0") {
            contentType(ContentType.Application.Json)
            headers {
                append(HttpHeaders.Authorization, "Bearer $token")
            }
        }

        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("Velocity: -16.0", response.bodyAsText())
    }
}
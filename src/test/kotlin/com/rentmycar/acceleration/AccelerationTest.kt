package com.rentmycar.acceleration

import com.rentmycar.BaseTest
import com.rentmycar.authentication.PasswordHasher
import com.rentmycar.dtos.requests.user.UserLoginRequest
import com.rentmycar.entities.User
import com.rentmycar.utils.UserRole
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class AccelerationTest : BaseTest() {

    @BeforeTest
    fun setupUser() {
        val hashedPassword = PasswordHasher.hashPassword("password123")
        transaction {
            User.new {
                firstName = "John"
                lastName = "Doe"
                username = "johndoe"
                email = "johndoe@example.com"
                password = hashedPassword
                role = UserRole.DEFAULT
            }
        }
    }

    private suspend fun getToken(client: HttpClient, email: String = "testuser4444@gmail.com"): String {
        val response = client.post("/user/login") {
            contentType(ContentType.Application.Json)
            setBody(
                Json.encodeToString(
                    UserLoginRequest.serializer(), UserLoginRequest(
                        email = email,
                        password = "password123"
                    )
                )
            )
        }

        val data = Json.decodeFromString<Map<String, String>>(response.bodyAsText())
        return data["token"] ?: throw Exception("No token")
    }

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
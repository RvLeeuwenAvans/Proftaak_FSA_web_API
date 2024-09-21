package com.rentmycar.user

import com.rentmycar.BaseTest
import com.rentmycar.requests.RegistrationRequest
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import junit.framework.TestCase.assertEquals
import kotlinx.serialization.json.Json
import kotlin.test.Test

class RegistrationTest : BaseTest() {

    private val validRegistrationRequest = RegistrationRequest(
        firstName = "John",
        lastName = "Doe",
        username = "johndoe",
        email = "johndoe@example.com",
        password = "password123"
    )

    private suspend fun registerUser(client: HttpClient, registrationRequest: RegistrationRequest): HttpResponse =
        client.post("/register") {
            contentType(ContentType.Application.Json)
            setBody(Json.encodeToString(RegistrationRequest.serializer(), registrationRequest))
        }

    @Test
    fun testSuccessfulRegistration() = withTestApplication {
        val response = registerUser(client, validRegistrationRequest)
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("User registered successfully", response.bodyAsText())
    }

    @Test
    fun testRegistrationFailsForDuplicateEmail() = withTestApplication {
        // Register the first time
        registerUser(client, validRegistrationRequest)

        // Try to register again with the same email
        val response = registerUser(client, validRegistrationRequest.copy(username = "john"))
        assertEquals(HttpStatusCode.Conflict, response.status)
        assertEquals("User with this email already exists", response.bodyAsText())
    }

    @Test
    fun testRegistrationFailsForDuplicateUsername() = withTestApplication {
        // Register the first time
        registerUser(client, validRegistrationRequest)

        // Try to register again with the same username
        val response = registerUser(client, validRegistrationRequest.copy(email = "john@example.com"))
        assertEquals(HttpStatusCode.Conflict, response.status)
        assertEquals("User with this username already exists", response.bodyAsText())
    }

    @Test
    fun testRegistrationFailsForInvalidData() = withTestApplication {
        val invalidRegistrationRequest = RegistrationRequest(
            firstName = "",
            lastName = "",
            username = "",
            email = "invalid-email",
            password = "short"
        )

        val response = registerUser(client, invalidRegistrationRequest)
        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertEquals(
            "Invalid registration data: First name cannot be empty, Last name cannot be empty, Username cannot be empty, Email must be valid, Password must be at least 6 characters long",
            response.bodyAsText()
        )
    }
}

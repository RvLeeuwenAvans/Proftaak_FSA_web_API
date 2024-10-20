package com.rentmycar.user

import com.rentmycar.BaseTest
import com.rentmycar.authentication.PasswordHasher
import com.rentmycar.entities.User
import com.rentmycar.requests.user.UserLoginRequest
import com.rentmycar.utils.UserRole
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import junit.framework.TestCase.assertEquals
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertContains

class LoginTest : BaseTest() {

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

    private val validLoginRequest = UserLoginRequest(
        email = "johndoe@example.com",
        password = "password123"
    )

    private suspend fun loginUser(client: HttpClient, loginRequest: UserLoginRequest): HttpResponse =
        client.post("/user/login") {
            contentType(ContentType.Application.Json)
            setBody(Json.encodeToString(UserLoginRequest.serializer(), loginRequest))
        }

    @Test
    fun testSuccessfulLogin() = withTestApplication {
        val response = loginUser(client, validLoginRequest)
        assertEquals(HttpStatusCode.OK, response.status)
        val responseBody = Json.decodeFromString<Map<String, String>>(response.bodyAsText())
        assert(responseBody.containsKey("token")) { "Response does not contain token" }
    }

    @Test
    fun testLoginFailsForInvalidCredentials() = withTestApplication {
        val response = loginUser(client, validLoginRequest.copy(password = "wrongpassword"))
        assertEquals(HttpStatusCode.Unauthorized, response.status)
        assertEquals("{\"error\":\"Password is not correct\"}", response.bodyAsText())
    }

    @Test
    fun testLoginFailsForUnregisteredEmail() = withTestApplication {
        val response = loginUser(client, validLoginRequest.copy(email = "unknown@example.com"))
        assertEquals(HttpStatusCode.NotFound, response.status)
        assertContains("{\"error\":\"User with email unknown@example.com not found\"}", response.bodyAsText())
    }

    @Test
    fun testLoginFailsForInvalidData() = withTestApplication {
        val invalidLoginRequest = UserLoginRequest(
            email = "invalid-email",
            password = ""
        )

        val response = loginUser(client, invalidLoginRequest)
        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertEquals(
            "{\"errors\":[\"Email must be valid\",\"Password cannot be empty\"]}",
            response.bodyAsText()
        )
    }
}
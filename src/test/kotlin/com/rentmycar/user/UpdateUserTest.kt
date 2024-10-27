package com.rentmycar.user

import com.rentmycar.BaseTest
import com.rentmycar.dtos.requests.user.UserRegistrationRequest
import com.rentmycar.dtos.requests.user.UserUpdateRequest
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
import kotlin.test.Test
import kotlin.test.assertEquals

val usersSeedDataUpdate = mutableListOf<UserRegistrationRequest>(
    UserRegistrationRequest(firstName = "Test", lastName = "User", username = "testuser4444", email = "testuser4444@gmail.com", password = "fakepwd"),
)

class UpdateUserTest: BaseTest(usersSeedDataUpdate) {
    private suspend fun updateUser(
        client: HttpClient,
        request: UserUpdateRequest,
        includeToken: Boolean = true
    ): HttpResponse {
        val token = getToken(client)

        return client.put("/user/update") {
            contentType(ContentType.Application.Json)
            setBody(Json.encodeToString(UserUpdateRequest.serializer(), request))
            headers {
                if (includeToken)
                    append(HttpHeaders.Authorization, "Bearer $token")
            }
        }
    }

    private val request1 = UserUpdateRequest(
        firstName = "Test upd",
        lastName = "User upd",
        username = "testuser4444upd",
        email = "testuser4444upd@gmail.com",
        password = "fakepwdupd"
    )
    private val request2 = UserUpdateRequest(
        firstName = "Test upd",
        lastName = "User upd",
    )
    private val request3 = UserUpdateRequest(firstName = "")
    private val request4 = UserUpdateRequest(lastName = "")
    private val request5 = UserUpdateRequest(username = "")
    private val request6 = UserUpdateRequest(email = "test.user.com")
    private val request7 = UserUpdateRequest(password = "1234")

    @Test
    fun successfulRequestUpdateAll() = withTestApplication {
        val response = updateUser(client, request1)
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("User updated successfully", response.bodyAsText())
    }

    @Test
    fun successfulRequestUpdatePartially() = withTestApplication {
        val response = updateUser(client, request2)
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("User updated successfully", response.bodyAsText())
    }

    @Test
    fun firstNameBlank() = withTestApplication {
        val response = updateUser(client, request3)
        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertEquals("{\"errors\":[\"First name cannot be empty\"]}", response.bodyAsText())
    }

    @Test
    fun lastNameBlank() = withTestApplication {
        val response = updateUser(client, request4)
        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertEquals("{\"errors\":[\"Last name cannot be empty\"]}", response.bodyAsText())
    }

    @Test
    fun usernameBlank() = withTestApplication {
        val response = updateUser(client, request5)
        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertEquals("{\"errors\":[\"Username cannot be empty\"]}", response.bodyAsText())
    }

    @Test
    fun emailInvalid() = withTestApplication {
        val response = updateUser(client, request6)
        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertEquals("{\"errors\":[\"Email must be valid\"]}", response.bodyAsText())
    }

    @Test
    fun passwordTooSmall() = withTestApplication {
        val response = updateUser(client, request7)
        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertEquals("{\"errors\":[\"Password must be at least 6 characters long\"]}", response.bodyAsText())
    }

    @Test
    fun unauthorizedRequest() = withTestApplication {
        val response = updateUser(client, request1, false)
        assertEquals(HttpStatusCode.Unauthorized, response.status)
    }
}
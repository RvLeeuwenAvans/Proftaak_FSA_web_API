package com.rentmycar.user

import com.rentmycar.BaseTest
import com.rentmycar.dtos.requests.user.UserRegistrationRequest
import io.ktor.client.HttpClient
import io.ktor.client.request.delete
import io.ktor.client.request.headers
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlin.test.Test
import kotlin.test.assertEquals

val usersSeedDataDelete = mutableListOf<UserRegistrationRequest>(
    UserRegistrationRequest(firstName = "Test", lastName = "User", username = "testuser4444", email = "testuser4444@gmail.com", password = "fakepwd"),
)

class DeleteUserTest: BaseTest(usersSeedDataDelete) {
    private suspend fun deleteUser(
        client: HttpClient,
        includeToken: Boolean = true
    ): HttpResponse {
        val token = getToken(client)

        return client.delete("/user/delete") {
            contentType(ContentType.Application.Json)
            headers {
                if (includeToken)
                    append(HttpHeaders.Authorization, "Bearer $token")
            }
        }
    }

    @Test
    fun successfullyDelete() = withTestApplication {
        val response = deleteUser(client)
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("User deleted successfully", response.bodyAsText())
    }

    @Test
    fun failedDelete() = withTestApplication {
        val response = deleteUser(client, false)
        assertEquals(HttpStatusCode.Unauthorized, response.status)
    }
}
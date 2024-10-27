package com.rentmycar

import com.rentmycar.authentication.PasswordHasher
import com.rentmycar.dtos.requests.user.UserLoginRequest
import com.rentmycar.dtos.requests.user.UserRegistrationRequest
import com.rentmycar.entities.Cars
import com.rentmycar.entities.Notifications
import com.rentmycar.entities.Users
import com.rentmycar.entities.seeders.Seeder
import com.rentmycar.plugins.configureDatabases
import com.rentmycar.utils.UserRole
import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.server.config.*
import io.ktor.server.testing.*
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.test.AfterTest
import kotlin.test.BeforeTest

abstract class BaseTest(
    private val usersSeedData: MutableList<UserRegistrationRequest> = mutableListOf()
) {

    @BeforeTest
    fun setup() {
        testApplication {
            environment { config = ApplicationConfig("application-test.conf") }
            application {
                configureDatabases(seed = false)
            }
        }

        transaction {
            val seeder = Seeder()
            seeder.seedBrands()
            seeder.seedModels()

            for ((index, user) in usersSeedData.withIndex()) {
                Users.insert {
                    it[id] = index + 1
                    it[firstName] = user.firstName
                    it[lastName] = user.lastName
                    it[username] = user.username
                    it[email] = user.email
                    it[password] = PasswordHasher.hashPassword(user.password)
                    it[role] = UserRole.ADMIN
                }
            }
        }
    }

    protected suspend fun getToken(client: HttpClient, email: String = "testuser4444@gmail.com"): String {
        val response = client.post("/user/login") {
            contentType(ContentType.Application.Json)
            setBody(Json.encodeToString(
                UserLoginRequest.serializer(), UserLoginRequest(
                    email = email,
                    password = "fakepwd"
                )))
        }

        val data = Json.decodeFromString<Map<String, String>>(response.bodyAsText())
        return data["token"] ?: throw Exception("No token")
    }

    fun <R> withTestApplication(test: suspend ApplicationTestBuilder.() -> R) {
        testApplication {
            environment { config = ApplicationConfig("application-test.conf") }
            test()
        }
    }

    @AfterTest
    fun tearDown() {
        transaction {
            Users.deleteAll()
            Cars.deleteAll()
            Notifications.deleteAll()
        }
    }
}

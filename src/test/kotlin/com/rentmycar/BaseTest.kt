package com.rentmycar

import com.rentmycar.entities.Users
import com.rentmycar.plugins.configureDatabases
import io.ktor.server.config.*
import io.ktor.server.testing.*
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.test.AfterTest
import kotlin.test.BeforeTest

abstract class BaseTest {

    @BeforeTest
    fun setup() {
        testApplication {
            environment { config = ApplicationConfig("application.conf") }
            application {
                configureDatabases()
            }
        }
    }

    fun <R> withTestApplication(test: suspend ApplicationTestBuilder.() -> R) {
        testApplication {
            environment { config = ApplicationConfig("application.conf") }
            test()
        }
    }

    @AfterTest
    fun tearDown() {
        transaction {
            Users.deleteAll()
        }
    }
}

package com.rentmycar

import com.rentmycar.routing.accelerationRoutes
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.testing.*
import kotlin.test.Test
import kotlin.test.assertEquals

class AccelerationServiceIntegrationTest {

    @Test
    fun testCalculateAcceleration() = testApplication {
        application {
            routing {
                accelerationRoutes()
            }
        }
        val response = client.get("/acceleration/magnitude?ax=3.0&ay=4.0&az=0.0")
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("Acceleration Magnitude: 5.0", response.bodyAsText())
    }

    @Test
    fun testCalculateVelocity() = testApplication {
        application {
            routing {
                accelerationRoutes()
            }
        }
        val response = client.get("/acceleration/velocity?initialVelocity=10.0&acceleration=2.0&deltaTime=3.0")
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("Velocity: 16.0", response.bodyAsText())
    }

    @Test
    fun testCalculateAccelerationWithZeroValues() = testApplication {
        application {
            routing {
                accelerationRoutes()
            }
        }
        val response = client.get("/acceleration/magnitude?ax=0.0&ay=0.0&az=0.0")
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("Acceleration Magnitude: 0.0", response.bodyAsText())
    }

    @Test
    fun testCalculateVelocityWithNegativeValues() = testApplication {
        application {
            routing {
                accelerationRoutes()
            }
        }
        val response = client.get("/acceleration/velocity?initialVelocity=-10.0&acceleration=-2.0&deltaTime=3.0")
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("Velocity: -16.0", response.bodyAsText())
    }
}
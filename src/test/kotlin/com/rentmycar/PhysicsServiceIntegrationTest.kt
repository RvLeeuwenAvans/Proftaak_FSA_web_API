package com.rentmycar

import com.rentmycar.services.PhysicsService
import io.ktor.server.testing.*
import kotlin.test.Test
import kotlin.test.assertEquals

class PhysicsServiceIntegrationTest {

    private val physicsService = PhysicsService()

    @Test
    fun testCalculateAccelerationMagnitude() = withTestApplication {
        val result = physicsService.calculateAccelerationMagnitude(3.0, 4.0, 0.0)
        assertEquals(5.0, result)
    }

    @Test
    fun testCalculateVelocity() = withTestApplication {
        val result = physicsService.calculateVelocity(10.0, 2.0, 3.0)
        assertEquals(16.0, result)
    }
}
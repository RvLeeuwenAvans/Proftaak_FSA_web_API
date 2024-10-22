package com.rentmycar

import com.rentmycar.controllers.PhysicsService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class PhysicsServiceIntegrationTest {

    private val physicsService = PhysicsService()

    @Test
    fun testCalculateAccelerationMagnitude() {
        val ax = 3.0
        val ay = 4.0
        val az = 0.0
        val expectedMagnitude = 5.0

        val result = physicsService.calculateAccelerationMagnitude(ax, ay, az)
        assertEquals(expectedMagnitude, result)
    }

    @Test
    fun testCalculateVelocity() {
        val initialVelocity = 10.0
        val acceleration = 2.0
        val deltaTime = 3.0
        val expectedVelocity = 16.0

        val result = physicsService.calculateVelocity(initialVelocity, acceleration, deltaTime)
        assertEquals(expectedVelocity, result)
    }
}
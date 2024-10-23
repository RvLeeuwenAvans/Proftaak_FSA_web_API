package com.rentmycar.controllers

class PhysicsService {
    fun calculateAccelerationMagnitude(ax: Double, ay: Double, az: Double): Double {
        return Math.sqrt(ax * ax + ay * ay + az * az)
    }

    fun calculateVelocity(initialVelocity: Double, acceleration: Double, deltaTime: Double): Double {
        return initialVelocity + acceleration * deltaTime
    }
}
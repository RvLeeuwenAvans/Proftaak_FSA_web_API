package com.rentmycar.services

import kotlin.math.sqrt

class AccelerationService {
    fun calculateVelocity(initialVelocity: Double, acceleration: Double, deltaTime: Double): Double {
        return initialVelocity + acceleration * deltaTime
    }

    fun calculateAccelerationMagnitude(ax: Double, ay: Double, az: Double): Double {
        return sqrt(ax * ax + ay * ay + az * az)
    }
}
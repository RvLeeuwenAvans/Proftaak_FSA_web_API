package com.rentmycar.services

import kotlin.math.sqrt

class AccelerationService {
    fun calculateVelocity(initialVelocity: Double, acceleration: Double, deltaTime: Double): Double {
        // speed + acceleration / time acceleration
        return initialVelocity + acceleration * deltaTime
    }

    fun calculateAccelerationMagnitude(ax: Double, ay: Double, az: Double): Double {
        //https://www.xylenepower.com/Vector%20Identities.htm
        //Ax = Vector A's magnitude component parallel to x axis;
        //Ay = Vector A's magnitude component parallel to y axis;
        //Az = Vector A's magnitude component parallel to z axis;
        return sqrt(ax * ax + ay * ay + az * az)
    }
}
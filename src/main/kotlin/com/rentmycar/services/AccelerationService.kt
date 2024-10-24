package com.rentmycar.services

import kotlin.math.sqrt

/**
 * References:
 *
 * https://help.gpsinsight.com/best-practice/defining-thresholds-for-your-fleet/
 * https://www.geotab.com/blog/what-is-g-force/#:~:text=break%20these%20rules.-,Harsh%20acceleration%20and%20harsh%20braking,-The%20harsh%20acceleration
 */
const val G: Double = 9.8
const val MAX_AY: Double = G * 0.37
const val MAX_AX: Double = MAX_AY / 2
const val MAX_AZ: Double = MAX_AY / 2

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

    fun getScore(ax: Float, ay: Float, az: Float): Int {
        var score = 100

        // Detect sharp turns.
        if (ax !in -MAX_AX..MAX_AX) score -= 5
        // Detect aggressive acceleration / deceleration.
        if (ay !in -MAX_AY..MAX_AY) score -= 10
        // Detect harsh vertical movement.
        if (az !in -MAX_AZ..MAX_AZ) score -= 5

        return score.coerceAtLeast(0)
    }
}
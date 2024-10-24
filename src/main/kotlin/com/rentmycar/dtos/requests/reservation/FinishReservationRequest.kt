package com.rentmycar.dtos.requests.reservation

import com.rentmycar.services.exceptions.RequestValidationException
import com.rentmycar.utils.*
import kotlinx.serialization.Serializable
import kotlin.math.pow
import kotlin.math.sqrt

@Serializable
data class FinishReservationRequest(
    val reservationId: Int,
    val distance: Double,
    val accelerationData: List<List<Double>>,
) {
    fun validate() {
        val errors = mutableListOf<String>()

        if (reservationId <= 0) errors.add("Reservation ID is invalid.")
        if (distance <= 0.0) errors.add("Distance is invalid.")
        if (accelerationData.isEmpty()) errors.add("Acceleration is empty.")
        if (!accelerationData.all { it.size == 3 }) errors.add("Acceleration is invalid.")

        if (errors.isNotEmpty())
            throw RequestValidationException(errors)
    }

    val averageAcceleration = accelerationData.fold(0.0) { sum, item ->
        sum + sqrt(item[0].pow(2) + item[1].pow(2) + item[2].pow(2))
    }

    fun getScore(): Int {
        var score = 100

        for ((ax, ay, az) in accelerationData) {
            // Detect sharp turns.
            if (ax !in -MAX_AX..MAX_AX) score -= 5
            // Detect aggressive acceleration / deceleration.
            if (ay !in -MAX_AY..MAX_AY) score -= 10
            // Detect harsh vertical movement.
            if (az !in -MAX_AZ..MAX_AZ) score -= 5
        }

        return score.coerceAtLeast(0)
    }
}
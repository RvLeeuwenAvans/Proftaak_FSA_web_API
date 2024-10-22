package com.rentmycar.utils

import kotlin.math.*

fun String.isNumeric(): Boolean = this.all { char -> char.isDigit() }

fun sanitizeId(id: String? = null): Int =
    if (id == null || !id.isNumeric()) -1 else id.toInt()

data class LocationData(val latitude: Double, val longitude: Double, val radius: Int)

/**
 * Calculates the distance between 2 coordinates given longitude and latitude of both.
 * Will return the result in meters.
 *
 * source: https://en.wikipedia.org/wiki/Haversine_formula
 */
fun haversine(
    startLatitude: Double,
    startLongitude: Double,
    endLatitude: Double,
    endLongitude: Double,
): Double {
    val r = 6371000
    val phi1 = Math.toRadians(startLatitude)
    val phi2 = Math.toRadians(endLatitude)
    val deltaPhi = Math.toRadians(endLatitude - startLatitude)
    val deltaLambda = Math.toRadians(endLongitude - startLongitude)

    val halfChordLength = sin(deltaPhi / 2.0).pow(2) +
            cos(phi1) * cos(phi2) * sin(deltaLambda / 2.0).pow(2)
    val angularDistance = 2 * atan2(sqrt(halfChordLength), sqrt(1 - halfChordLength))

    return floor(r * angularDistance)
}

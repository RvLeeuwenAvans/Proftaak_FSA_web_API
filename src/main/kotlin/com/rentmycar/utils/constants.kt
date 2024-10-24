package com.rentmycar.utils

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

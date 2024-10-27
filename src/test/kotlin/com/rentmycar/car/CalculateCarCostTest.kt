package com.rentmycar.car

import com.rentmycar.services.CarBO
import io.mockk.mockkClass
import kotlin.test.Test
import kotlin.test.assertEquals

class CalculateCarCostTest {
    private val carBO = mockkClass(CarBO::class)

    @Test
    fun testFuelEfficiencyCalculation() {
        val kilometersPerLitre = 20.0
        val fuelCostPerLitre = 1.00
        // with a fuel efficiency of 20km per litre, and a fuel cost of 1.00 per litre,
        // the fuel cost per kilometer should be 0.05 cents
        val fuelCostPerKilometer = fuelCostPerLitre * carBO.calculateFuelUsagePerKilometer(kilometersPerLitre)
        assertEquals(0.05, fuelCostPerKilometer)

        // with fewer kilometers per litre, we expect the price per kilometer to be higher: 0.10 cents.
        // even though the fuel price remains te same
        val lowerkilometersPerLitre = 10.0
        val HigherfuelCostPerKilometer =
            fuelCostPerLitre * carBO.calculateFuelUsagePerKilometer(lowerkilometersPerLitre)
        assertEquals(0.1, HigherfuelCostPerKilometer)
    }
}
package com.rentmycar.utils

enum class UserRole { DEFAULT, ADMIN; }

/**
 * ICE: Internal combustion engine
 * BEV: battery electric vehicle
 * FCEV: Fuel cell electric vehicle
 */
enum class Category {
    ICE, BEV, FCEV;

    companion object {
        val categories by lazy { Category.entries.map { it.name } }
    }
}

enum class Transmission {
    AUTOMATIC, MANUAL;

    companion object {
        val transmissions by lazy { Transmission.entries.map { it.name } }
    }
}

enum class FuelType(
    val category: Category,
    // ideally the price per unit is retrieved live via external tracking API; for the sake of scope & ease
    // Each fuel has a predefined associated price per unit (kw/h, litre, etc.)
    val pricePerUnit: Int
) {
    DIESEL(Category.ICE, 12),
    PETROL(Category.ICE, 10),
    GAS(Category.ICE, 9),
    ELECTRIC(Category.BEV, 8),
    HYDROGEN(Category.FCEV, 20);

    companion object {
        val fuelTypes by lazy { FuelType.entries.map { it.name } }
    }
}
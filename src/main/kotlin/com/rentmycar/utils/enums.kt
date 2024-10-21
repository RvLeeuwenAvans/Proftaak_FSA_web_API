package com.rentmycar.utils

enum class UserRole {
    DEFAULT, ADMIN;

    companion object {
        val userRoles by lazy { UserRole.entries.map { it.name } }
    }
}

enum class Category { ICE, BEV, FCEV }

enum class Transmission {
    AUTOMATIC, MANUAL;

    companion object {
        val transmissions by lazy { Transmission.entries.map { it.name } }
    }
}

enum class FuelType(val category: Category, val pricePerUnit: Int) {
    DIESEL(Category.ICE, 12),
    PETROL(Category.ICE, 10),
    GAS(Category.ICE, 9),
    ELECTRIC(Category.BEV, 8),
    HYDROGEN(Category.FCEV, 20);

    companion object {
        val fuelTypes by lazy { FuelType.entries.map { it.name } }
    }
}
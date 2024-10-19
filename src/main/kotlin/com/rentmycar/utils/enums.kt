package com.rentmycar.utils

enum class UserRole {
    DEFAULT, ADMIN;

    companion object {
        val userRoles by lazy { UserRole.entries.map { it.name } }
    }
}

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

enum class FuelType {
    DIESEL, PETROL, GAS, ELECTRIC, HYDROGEN;

    companion object {
        val fuelTypes by lazy { FuelType.entries.map { it.name } }
    }
}
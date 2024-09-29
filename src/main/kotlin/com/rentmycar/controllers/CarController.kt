package com.rentmycar.controllers

import com.rentmycar.authentication.JWTConfig
import com.rentmycar.repositories.CarRepository

class CarController(private val config: JWTConfig) {

    private val carRepository = CarRepository()

}
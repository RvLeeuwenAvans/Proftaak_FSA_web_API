package com.rentmycar.controllers

import com.rentmycar.authentication.JWTConfig
import com.rentmycar.repositories.UserRepository

class ReservationController(private val config: JWTConfig) {

    private val userRepository = UserRepository()

}
package com.rentmycar.controllers

import com.rentmycar.authentication.JWTConfig
import com.rentmycar.repositories.ReservationRepository

class ReservationController(private val config: JWTConfig) {

    private val reservationRepository = ReservationRepository()

}
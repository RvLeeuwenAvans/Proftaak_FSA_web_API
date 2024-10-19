package com.rentmycar.services

import com.rentmycar.repositories.LocationRepository

class LocationService {
    private val locationRepository = LocationRepository()

    fun getByCar(id: Int) = locationRepository.getByCar(id)
}
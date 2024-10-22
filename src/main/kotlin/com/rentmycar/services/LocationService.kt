package com.rentmycar.services

import com.rentmycar.repositories.LocationRepository
import com.rentmycar.requests.location.LocationRequest

class LocationService {
    private val locationRepository = LocationRepository()

    fun getByCar(id: Int) = locationRepository.getByCar(id)

    fun addLocation(request: LocationRequest) {
        val car = CarService().getCar(request.carId)
        locationRepository.createLocation(car = car, longitude = request.longitude, latitude = request.latitude)
    }

    fun updateLocation(request: LocationRequest) {
        locationRepository.updateLocation(
            carId = request.carId,
            longitude = request.longitude,
            latitude = request.latitude
        )
    }
}
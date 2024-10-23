package com.rentmycar.services

import com.rentmycar.entities.User
import com.rentmycar.repositories.LocationRepository
import com.rentmycar.requests.location.LocationRequest

class LocationService {
    private val locationRepository = LocationRepository()

    fun getByCar(id: Int) = locationRepository.getByCar(id)

    fun addLocation(request: LocationRequest, user: User) {
        val car = CarService().getCar(request.carId)
        CarService().ensureCarOwner(user, request.carId)
        locationRepository.createLocation(car = car, longitude = request.longitude, latitude = request.latitude)
    }

    fun updateLocation(request: LocationRequest, user: User) {
        CarService().ensureCarOwner(user, request.carId)
        locationRepository.updateLocation(
            carId = request.carId,
            longitude = request.longitude,
            latitude = request.latitude
        )
    }
}
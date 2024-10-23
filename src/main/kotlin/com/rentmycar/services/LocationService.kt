package com.rentmycar.services

import com.rentmycar.dtos.requests.location.LocationRequest
import com.rentmycar.entities.User
import com.rentmycar.repositories.LocationRepository

class LocationService {
    private val locationRepository = LocationRepository()

    fun getByCar(id: Int) = locationRepository.getByCar(id)

    fun addLocation(user: User, request: LocationRequest) {
        CarService.ensureUserIsCarOwner(user, request.carId)

        val car = CarService.getBusinessObject(request.carId).getCar()
        locationRepository.createLocation(car = car, longitude = request.longitude, latitude = request.latitude)
    }

    fun updateLocation(user: User, request: LocationRequest) {
        CarService.ensureUserIsCarOwner(user, request.carId)

        locationRepository.updateLocation(
            carId = request.carId,
            longitude = request.longitude,
            latitude = request.latitude
        )
    }
}
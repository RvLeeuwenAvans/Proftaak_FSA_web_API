package com.rentmycar.services

import com.rentmycar.entities.toDTO
import com.rentmycar.repositories.LocationRepository
import com.rentmycar.dtos.requests.location.LocationRequest

class LocationService {
    private val locationRepository = LocationRepository()

    fun getByCar(id: Int) = locationRepository.getByCar(id)

    fun addLocation(request: LocationRequest) {
        val car = CarService.getBusinessObject(request.carId).getCar()
        locationRepository.createLocation(car = car, longitude = request.longitude, latitude = request.latitude)
    }

    fun updateLocation(request: LocationRequest) {
        val car = CarService.getBusinessObject(request.carId).getCar()
        locationRepository.updateLocation(
            carId = car.toDTO().id,
            longitude = request.longitude,
            latitude = request.latitude
        )
    }
}
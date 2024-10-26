package com.rentmycar.services

import com.rentmycar.dtos.requests.location.LocationRequest
import com.rentmycar.entities.User
import com.rentmycar.repositories.CarRepository
import com.rentmycar.repositories.LocationRepository
import com.rentmycar.services.exceptions.NotFoundException

class LocationService {
    private val locationRepository = LocationRepository()

    fun getByCar(id: Int) = locationRepository.getByCar(id)

    fun addLocation(user: User, request: LocationRequest) {
        CarService.ensureUserIsCarOwner(user, request.carId)

        val car = CarService.getBusinessObject(request.carId).getCar()

        try {
            // try to update car location, if the car has non create it.
            locationRepository.getByCar(carId = request.carId)
            updateLocation(user, request)
        } catch (e: NotFoundException) {
            val location =
                locationRepository.createLocation(car = car, longitude = request.longitude, latitude = request.latitude)
            CarRepository().updateCar(request.carId, location = location)
        }
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
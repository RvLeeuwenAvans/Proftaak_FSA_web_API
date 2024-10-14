package com.rentmycar.services

import com.rentmycar.entities.User
import com.rentmycar.repositories.CarRepository
import com.rentmycar.services.exceptions.CarNotFoundException

class CarService {
    private val carRepository = CarRepository()

    fun getCar(id: Int) = carRepository.getCarById(id) ?: throw CarNotFoundException(id)

    fun isCarOwner(user: User, carId: Int): Boolean =
        user.id.value == getCar(carId).ownerId.value
}
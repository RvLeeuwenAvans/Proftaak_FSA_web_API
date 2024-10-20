package com.rentmycar.services

import com.rentmycar.entities.Car
import com.rentmycar.entities.Model
import com.rentmycar.entities.User
import com.rentmycar.repositories.CarRepository
import com.rentmycar.requests.car.RegisterCarRequest
import com.rentmycar.requests.car.UpdateCarRequest
import com.rentmycar.services.exceptions.AlreadyExistsException
import com.rentmycar.services.exceptions.NotAllowedException
import com.rentmycar.services.exceptions.NotFoundException

class CarService {
    private val carRepository = CarRepository()

    fun getCar(id: Int) = carRepository.getCarById(id) ?: throw NotFoundException("Car with id: $id not found")

    fun delete(user: User, carId: Int) {
        ensureCarOwner(user, carId)
        val timeslots = TimeSlotService().getTimeSlots(getCar(carId))
        if (timeslots.isNotEmpty()) {
            throw NotAllowedException("Car can not be deleted when it has linked timeslots")
        }
        carRepository.deleteCar(carId)
    }

    fun ensureCarOwner(user: User, carId: Int) {
        if (user != getCar(carId).owner) throw NotAllowedException("User does not own car with id: $carId")
    }

    private fun ensureLicensePlateIsUnique(licensePlate: String) {
        val car = carRepository.getCarByLicensePlate(licensePlate)
        if (car != null) {
            throw AlreadyExistsException("Car with licensePlate: $licensePlate already exists")
        }
    }

    fun register(user: User, model: Model, registrationRequest: RegisterCarRequest): Car {
        ensureLicensePlateIsUnique(registrationRequest.licensePlate)
        return carRepository.registerCar(
            user,
            registrationRequest.licensePlate,
            model,
            registrationRequest.fuel,
            registrationRequest.year,
            registrationRequest.color,
            registrationRequest.transmission,
            registrationRequest.price
        )
    }

    fun update(user: User, updateRequest: UpdateCarRequest): Car {
        ensureCarOwner(user, updateRequest.carId)
        return carRepository.updateCar(
            updateRequest.carId,
            updateRequest.year,
            updateRequest.color,
            updateRequest.transmission,
            updateRequest.price,
            updateRequest.fuel
        )
    }


}
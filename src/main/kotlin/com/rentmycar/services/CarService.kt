package com.rentmycar.services

import com.rentmycar.entities.Model
import com.rentmycar.entities.User
import com.rentmycar.entities.toDTO
import com.rentmycar.entities.Car as CarDAO
import com.rentmycar.repositories.CarRepository
import com.rentmycar.routing.controllers.requests.car.RegisterCarRequest
import com.rentmycar.routing.controllers.requests.car.UpdateCarRequest
import com.rentmycar.services.exceptions.AlreadyExistsException
import com.rentmycar.services.exceptions.NotAllowedException
import com.rentmycar.utils.FuelType
import com.rentmycar.utils.Transmission

private const val AVERAGE_KILOMETERS_PER_YEAR = 15000

class CarService {
    private val carRepository = CarRepository()

    fun register(user: User, model: Model, registrationRequest: RegisterCarRequest): CarBO {
        ensureLicensePlateIsUnique(registrationRequest.licensePlate)
        val fuelType = registrationRequest.transmission.let { FuelType.valueOf(it.uppercase()) }

        val carId = carRepository.registerCar(
            user,
            registrationRequest.licensePlate,
            model,
            registrationRequest.fuel.let { FuelType.valueOf(it.uppercase()) },
            registrationRequest.year,
            registrationRequest.color,
            registrationRequest.transmission.let { Transmission.valueOf(it.uppercase()) },
            registrationRequest.price,
            fuelType.category
        ).id.value

        return getBusinessObject(user, carId)
    }

    private fun ensureLicensePlateIsUnique(licensePlate: String) {
        val car = carRepository.getCarByLicensePlate(licensePlate)
        if (car != null) {
            throw AlreadyExistsException("Car with licensePlate: $licensePlate already exists")
        }
    }

    companion object {
        fun getBusinessObject(user: User, carId: Int): CarBO {
            return CarBO.instatiateBusinessObject(user, carId)
        }
    }
}

abstract class CarBO(private val carDAO: CarDAO, private val carRepository: CarRepository) {
    private val carId = carDAO.id.value

    fun getCar() = carDAO

    fun delete() {
        val timeslots = TimeSlotService().getTimeSlots(carDAO)
        if (timeslots.isNotEmpty()) {
            throw NotAllowedException("Car can not be deleted when it has linked timeslots")
        }
        carRepository.deleteCar(carId)
    }

    fun update(user: User, updateRequest: UpdateCarRequest): CarBO {
        val fuelType = updateRequest.fuel?.let { FuelType.valueOf(it.uppercase()) }

        carRepository.updateCar(
            carId,
            updateRequest.year,
            updateRequest.color,
            updateRequest.transmission?.let { Transmission.valueOf(it.uppercase()) },
            updateRequest.price,
            fuelType,
            fuelType?.let { fuelType.category }
        )

        return instatiateBusinessObject(user, carId)
    }

    fun calculateTotalOwnershipCosts(): Double {
        return calculatePricePerKilometer() * AVERAGE_KILOMETERS_PER_YEAR
    }

    abstract fun calculatePricePerKilometer(kilometers: Int = 1): Double

    companion object {
        fun instatiateBusinessObject(user: User, carId: Int): CarBO {
            val carDAO = CarRepository().getCarById(carId)

            if (user != carDAO.owner) throw NotAllowedException("$user does not own car with id: $carId")

            return when (carDAO.toDTO().fuel) {
                FuelType.DIESEL, FuelType.PETROL, FuelType.GAS -> InternalCombustionEngine(carDAO)
                FuelType.ELECTRIC -> BatteryElectricVehicle(carDAO)
                FuelType.HYDROGEN -> FuelCellElectricVehicle(carDAO)
            }
        }

        private class BatteryElectricVehicle(car: CarDAO) : CarBO(car, CarRepository()) {
            override fun calculatePricePerKilometer(kilometers: Int): Double =
                getCar().fuel.pricePerUnit * (1.0 + (1.0 % 0.3))
        }

        private class InternalCombustionEngine(car: CarDAO) : CarBO(car, CarRepository()) {
            override fun calculatePricePerKilometer(kilometers: Int): Double =
                getCar().fuel.pricePerUnit * (1.0 + (1.0 % 0.4))
        }

        private class FuelCellElectricVehicle(car: CarDAO) : CarBO(car, CarRepository()) {
            override fun calculatePricePerKilometer(kilometers: Int): Double =
                getCar().fuel.pricePerUnit * (1.0 + (1.0 % 0.5))
        }
    }
}

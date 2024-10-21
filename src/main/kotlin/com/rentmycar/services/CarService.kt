package com.rentmycar.services

import com.rentmycar.entities.Car
import com.rentmycar.entities.Model
import com.rentmycar.entities.User
import com.rentmycar.entities.toDTO
import com.rentmycar.repositories.CarRepository
import com.rentmycar.routing.controllers.requests.car.RegisterCarRequest
import com.rentmycar.routing.controllers.requests.car.UpdateCarRequest
import com.rentmycar.services.exceptions.AlreadyExistsException
import com.rentmycar.services.exceptions.NotAllowedException
import com.rentmycar.utils.Category
import com.rentmycar.utils.FuelType
import com.rentmycar.utils.Transmission

class CarService {
    private val carRepository = CarRepository()

    fun getCar(id: Int) = carRepository.getCarById(id)

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
        val fuelType = registrationRequest.transmission.let { FuelType.valueOf(it.uppercase()) }

        return carRepository.registerCar(
            user,
            registrationRequest.licensePlate,
            model,
            registrationRequest.fuel.let { FuelType.valueOf(it.uppercase()) },
            registrationRequest.year,
            registrationRequest.color,
            registrationRequest.transmission.let { Transmission.valueOf(it.uppercase()) },
            registrationRequest.price,
            CarCategory.getCarCategory(fuelType).category
        )
    }

    fun update(user: User, updateRequest: UpdateCarRequest): Car {
        ensureCarOwner(user, updateRequest.carId)
        val fuelType = updateRequest.transmission?.let { FuelType.valueOf(it.uppercase()) }

        return carRepository.updateCar(
            updateRequest.carId,
            updateRequest.year,
            updateRequest.color,
            updateRequest.transmission?.let { Transmission.valueOf(it.uppercase()) },
            updateRequest.price,
            fuelType,
            fuelType?.let { CarCategory.getCarCategory(fuelType).category }
        )
    }

    fun getTotalCostOfOwnership(user: User, carId: Int): Double {
        ensureCarOwner(user, carId)
        val carType = CarCategory.getCarCategory(getCar(carId).toDTO().fuel)
        return carType.calculateTotalOwnershipCosts()
    }

    fun getPricePerKilometer(user: User, carId: Int): Double {
        ensureCarOwner(user, carId)
        val carType = CarCategory.getCarCategory(getCar(carId).toDTO().fuel)
        return carType.calculatePricePerKilometer()
    }

    sealed class CarCategory(val fuelType: FuelType, private val efficiencyFactor: Double) {
        val category: Category = fuelType.category

        fun calculateTotalOwnershipCosts(): Double {
            return calculatePricePerKilometer() * 15000
        }

        fun calculatePricePerKilometer(): Double {
            return  fuelType.pricePerUnit * (1.0 + (1.0 % efficiencyFactor))
        }

        private class BatteryElectricVehicle(fuelType: FuelType) : CarCategory(fuelType, 0.1)
        private class InternalCombustionEngine(fuelType: FuelType) : CarCategory(fuelType, 0.1)
        private class FuelCellElectricVehicle(fuelType: FuelType) : CarCategory(fuelType, 0.1)

        companion object {
            fun getCarCategory(fuelType: FuelType) = when (fuelType) {
                FuelType.DIESEL, FuelType.PETROL, FuelType.GAS ->
                    InternalCombustionEngine(fuelType)

                FuelType.ELECTRIC ->
                    BatteryElectricVehicle(fuelType)

                FuelType.HYDROGEN ->
                    FuelCellElectricVehicle(fuelType)
            }
        }
    }
}
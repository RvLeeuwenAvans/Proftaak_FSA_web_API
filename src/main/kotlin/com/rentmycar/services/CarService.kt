package com.rentmycar.services

import com.rentmycar.dtos.requests.car.RegisterCarRequest
import com.rentmycar.dtos.requests.car.UpdateCarRequest
import com.rentmycar.entities.Car
import com.rentmycar.entities.Model
import com.rentmycar.entities.User
import com.rentmycar.entities.toDTO
import com.rentmycar.repositories.CarRepository
import com.rentmycar.services.exceptions.AlreadyExistsException
import com.rentmycar.services.exceptions.NotAllowedException
import com.rentmycar.utils.FuelType
import com.rentmycar.utils.LocationData
import com.rentmycar.utils.Transmission
import com.rentmycar.entities.Car as CarDAO

/**
 * used to calculate the TCO of a car.
 *
 * source: https://www.cbs.nl/nl-nl/visualisaties/verkeer-en-vervoer/verkeer/verkeersprestaties-personenautos
 */
private const val AVERAGE_KILOMETERS_PER_YEAR = 12000

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

        return getBusinessObject(carId)
    }


    fun getCars(
        longitude: Double? = null,
        latitude: Double? = null,
        radius: Int? = null,
        ownerId: Int? = null,
        category: String? = null,
        minPrice: Int? = null,
        maxPrice: Int? = null
    ): List<Car> {
        // If radius is provided and not null, we can filter the cars by radius only if
        // coordinates (longitude and latitude) of the user are provided and valid.
        val locationData: LocationData? = radius?.let locationData@{ _radius ->
            longitude?.let { if (it in -90.0..90.0) return@locationData null }?.let {
                latitude?.let { if (it in -90.0..90.0) return@locationData null }?.let {
                    LocationData(
                        latitude,
                        longitude,
                        _radius
                    )
                }
            }
        }

        val filteredCars = CarRepository().getFilteredCars(
            ownerId = ownerId,
            category = category,
            minPrice = minPrice,
            maxPrice = maxPrice,
            locationData = locationData,
        )
        return filteredCars
    }

    private fun ensureLicensePlateIsUnique(licensePlate: String) {
        val car = carRepository.getCarByLicensePlate(licensePlate)
        if (car != null) {
            throw AlreadyExistsException("Car with licensePlate: $licensePlate already exists")
        }
    }

    companion object {
        fun ensureUserIsCarOwner(user: User, carId: Int) {
            if (user.id.value != CarRepository().getCarOwner(carId).id.value)
                throw NotAllowedException("$user does not own car with id: $carId")
        }

        fun getBusinessObject(carId: Int): CarBO {
            return CarBO.instantiateBusinessObject(carId)
        }
    }
}

/**
 * we differentiate between multiple types of car, each implementing their own ways to calculate associatedCosts;
 * The CarService explicitly instantiates a BusinessObject as opposed services, which are implicitly considered BOs
 * Instantiating the carBO, retrieves appropriate child class.
 */
abstract class CarBO(private val carDAO: CarDAO, private val carRepository: CarRepository) {
    private val carId = carDAO.id.value

    fun getCar() = carDAO

    fun delete(user: User) {
        CarService.ensureUserIsCarOwner(user, carId)

        val timeslots = TimeSlotService().getTimeSlots(carDAO)
        if (timeslots.isNotEmpty()) {
            throw NotAllowedException("Car can not be deleted when it has linked timeslots")
        }
        carRepository.deleteCar(carId)
    }

    fun update(user: User, updateRequest: UpdateCarRequest): CarBO {
        CarService.ensureUserIsCarOwner(user, carId)

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

        return instantiateBusinessObject(carId)
    }

    fun calculateTotalOwnershipCosts(user: User): Double {
        CarService.ensureUserIsCarOwner(user, carId)
        return calculatePricePerKilometer(user) * AVERAGE_KILOMETERS_PER_YEAR
    }

    abstract fun calculatePricePerKilometer(user: User, kilometers: Int = 1): Double

    companion object {
        fun instantiateBusinessObject(carId: Int): CarBO {
            val carDAO = CarRepository().getCarById(carId)

            return when (carDAO.toDTO().fuel) {
                FuelType.DIESEL, FuelType.PETROL, FuelType.GAS -> CarTypes.InternalCombustionEngine(carDAO)
                FuelType.ELECTRIC -> CarTypes.BatteryElectricVehicle(carDAO)
                FuelType.HYDROGEN -> CarTypes.FuelCellElectricVehicle(carDAO)
            }
        }

        /**
         * each car category has a differing variables for calculating the price per kilometer &
         * therefor implicitly the total ownership costs.
         *
         * The formula remains the same for each type:
         *      (fuel price * (1.0 + (difference between 100% fuel efficiency and engine efficiency)))
         *      ~ a higher fuel efficiency means, a lower price per kilometer and vice versa.
         */
        private object CarTypes {
            class BatteryElectricVehicle(car: CarDAO) : CarBO(car, CarRepository()) {
                override fun calculatePricePerKilometer(user: User, kilometers: Int): Double {
                    CarService.ensureUserIsCarOwner(user, getCar().toDTO().id)
                    // the BEV has a fuel efficiency of 0.3 = 30%, so the formula can be read as: (fuel price * 0.3)
                    return getCar().toDTO().fuel.pricePerUnit * (1.0 + (1.0 % 0.3))
                }
            }

            class InternalCombustionEngine(car: CarDAO) : CarBO(car, CarRepository()) {
                override fun calculatePricePerKilometer(user: User, kilometers: Int): Double {
                    CarService.ensureUserIsCarOwner(user, getCar().toDTO().id)
                    // the BEV has a fuel efficiency of 0.4 = 30%, so the formula can be read as: (fuel price * 0.4)
                    return getCar().toDTO().fuel.pricePerUnit * (1.0 + (1.0 % 0.4))
                }
            }

            class FuelCellElectricVehicle(car: CarDAO) : CarBO(car, CarRepository()) {
                override fun calculatePricePerKilometer(user: User, kilometers: Int): Double {
                    CarService.ensureUserIsCarOwner(user, getCar().toDTO().id)
                    // the BEV has a fuel efficiency of 1.5 = 150%, so the formula can be read as: (fuel price * 1.5)
                    return getCar().toDTO().fuel.pricePerUnit * (1.0 + (1.0 % 1.5))
                }
            }
        }
    }
}

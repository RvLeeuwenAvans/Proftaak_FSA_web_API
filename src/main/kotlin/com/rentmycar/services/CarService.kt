package com.rentmycar.services

import com.rentmycar.dtos.CarDTO
import com.rentmycar.dtos.requests.car.RegisterCarRequest
import com.rentmycar.dtos.requests.car.UpdateCarRequest
import com.rentmycar.entities.Car
import com.rentmycar.entities.Model
import com.rentmycar.entities.User
import com.rentmycar.entities.toDTO
import com.rentmycar.repositories.CarRepository
import com.rentmycar.services.exceptions.AlreadyExistsException
import com.rentmycar.services.exceptions.NotAllowedException
import com.rentmycar.utils.Category.Companion.categories
import com.rentmycar.utils.FuelType
import com.rentmycar.utils.LocationData
import com.rentmycar.utils.Transmission
import com.rentmycar.entities.Car as CarDAO
import com.rentmycar.entities.Cars
import org.jetbrains.exposed.sql.transactions.transaction

/**
 * used to calculate the TCO of a car.
 *
 * source: https://www.cbs.nl/nl-nl/visualisaties/verkeer-en-vervoer/verkeer/verkeersprestaties-personenautos
 */
private const val AVERAGE_KILOMETERS_PER_YEAR = 12000

class CarService {
    private val carRepository = CarRepository()


fun getCarsByOwnerId(ownerId: Int): List<CarDTO> {
    return transaction {
        Car.find { Cars.user eq ownerId }.map { it.toDTO() }
    }
}


    fun register(user: User, model: Model, registrationRequest: RegisterCarRequest): CarBO {
        ensureLicensePlateIsUnique(registrationRequest.licensePlate)

        val fuelType = registrationRequest.fuel.let { FuelType.valueOf(it.uppercase()) }

        val carId = carRepository.registerCar(
            user,
            registrationRequest.licensePlate,
            model,
            fuelType,
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
        var _radius = radius
        // If radius is provided and not null, we can filter the cars by radius only if
        // coordinates (longitude and latitude) of the user are provided and valid.
        // Therefore:
        if (longitude == null || latitude == null || longitude !in -90.0..90.0 || latitude !in -90.0..90.0)
            _radius = null

        val filteredCars = CarRepository().getFilteredCars(
            ownerId = if (ownerId != -1) ownerId else null,
            category = if (categories.contains(category?.uppercase())) category else null,
            minPrice = minPrice,
            maxPrice = maxPrice,
            locationData = if (_radius != null) LocationData(
                latitude = latitude!!,
                longitude = longitude!!,
                radius = _radius
            ) else null,
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
                throw NotAllowedException("User does not own car with id: $carId")
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

    fun delete() {
        val timeslots = TimeSlotService().getTimeSlots(carDAO)
        if (timeslots.isNotEmpty()) {
            throw NotAllowedException("Car can not be deleted when it has linked timeslots")
        }
        carRepository.deleteCar(carId)
    }

    fun update(updateRequest: UpdateCarRequest): CarBO {
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

    fun calculateTotalOwnershipCosts(): Double {
        return calculatePricePerKilometer() * AVERAGE_KILOMETERS_PER_YEAR
    }

    /**
     * @param engineEfficiency
     *  takes a double value, eg: 1.0 meaning that for every kilometer 1 unit of fuel is expended.
     *  Can be read as litres per kilometer.
     *   ~ a higher fuel efficiency means, a lower price per kilometer.
     *   ~ ex: an engineEfficiency of 20.0 means 20 kilometers per litre.
     *
     *  @return Double
     *   the returns a multiplier in the form of a double.
     */
    internal fun calculateFuelUsagePerKilometer(engineEfficiency: Double): Double {
        return 1 / ((engineEfficiency / 1.0) * 1.0)
    }

    abstract fun calculatePricePerKilometer(kilometers: Int = 1): Double

    /**
     * each car category has a differing variables for calculating the price per kilometer &
     * therefor implicitly the total ownership costs.
     *
     * The formula remains the same for each type:
     *      (multiplicand (fuel price) * multiplier (FuelUsagePerKilometer))
     */
    private object CarTypes {
        class BatteryElectricVehicle(car: CarDAO) : CarBO(car, CarRepository()) {
            override fun calculatePricePerKilometer(kilometers: Int): Double {
                val fuelPrice = getCar().toDTO().fuel.pricePerUnit
                return kilometers * (fuelPrice * calculateFuelUsagePerKilometer(20.0))
            }
        }

        class InternalCombustionEngine(car: CarDAO) : CarBO(car, CarRepository()) {
            override fun calculatePricePerKilometer(kilometers: Int): Double {
                val fuelPrice = getCar().toDTO().fuel.pricePerUnit
                return kilometers * (fuelPrice * calculateFuelUsagePerKilometer(17.0))
            }
        }

        class FuelCellElectricVehicle(car: CarDAO) : CarBO(car, CarRepository()) {
            override fun calculatePricePerKilometer(kilometers: Int): Double {
                val fuelPrice = getCar().toDTO().fuel.pricePerUnit
                return kilometers * (fuelPrice * calculateFuelUsagePerKilometer(12.0))
            }
        }
    }

    companion object {
        fun instantiateBusinessObject(carId: Int): CarBO {
            val carDAO = CarRepository().getCarById(carId)

            return when (carDAO.toDTO().fuel) {
                FuelType.DIESEL, FuelType.PETROL, FuelType.GAS -> CarTypes.InternalCombustionEngine(carDAO)
                FuelType.ELECTRIC -> CarTypes.BatteryElectricVehicle(carDAO)
                FuelType.HYDROGEN -> CarTypes.FuelCellElectricVehicle(carDAO)
            }
        }
    }
}

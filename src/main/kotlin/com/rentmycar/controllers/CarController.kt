package com.rentmycar.controllers



import com.rentmycar.entities.Model
import com.rentmycar.entities.Fuel
import com.rentmycar.plugins.user
import com.rentmycar.repositories.CarRepository
import com.rentmycar.repositories.FuelRepository
import com.rentmycar.repositories.ModelRepository
import com.rentmycar.requests.car.RegisterCarRequest
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*

class CarController {

    private val carRepository = CarRepository()
    private val modelRepository = ModelRepository()  // Assuming you have a ModelRepository
    private val fuelRepository = FuelRepository()    // Assuming you have a FuelRepository

    // Register a new car
    suspend fun registerCar(call: ApplicationCall) {
        val user = call.user()

        val registrationRequest = call.receive<RegisterCarRequest>()
        val validationErrors = registrationRequest.validate()

        // Validate the request data
        if (validationErrors.isNotEmpty()) return call.respond(
            HttpStatusCode.BadRequest,
            "Invalid registration data: ${validationErrors.joinToString(", ")}"
        )

        // Check if the license plate already exists
        if (carRepository.doesLicensePlateExist(registrationRequest.licensePlate)) return call.respond(
            HttpStatusCode.Conflict,
            "License plate is already registered"
        )

        // Fetch model and fuel by their IDs (assuming repositories exist)
        val model = modelRepository.getModelById(registrationRequest.modelId)
            ?: return call.respond(HttpStatusCode.NotFound, "Model not found")

        val fuel = fuelRepository.getFuelById(registrationRequest.fuelId)
            ?: return call.respond(HttpStatusCode.NotFound, "Fuel type not found")

        // Register the car
        carRepository.registerCar(
            owner = user,
            licensePlate = registrationRequest.licensePlate,
            model = model,
            fuel = fuel,
            year = registrationRequest.year,
            color = registrationRequest.color,
            transmission = registrationRequest.transmission
        )

        call.respond(HttpStatusCode.OK, "Car registered successfully")
    }
}

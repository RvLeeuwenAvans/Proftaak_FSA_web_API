package com.rentmycar.services.exceptions

class CarNotFoundException(carId: Int): Exception("Car with id: $carId not found")
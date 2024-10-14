package com.rentmycar.services.exceptions

class TimeSlotNotFoundException(id: Int) : RuntimeException("Time slot with id: $id not found") {
}
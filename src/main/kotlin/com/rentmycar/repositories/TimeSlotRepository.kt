package com.rentmycar.repositories

import com.rentmycar.entities.Car
import com.rentmycar.entities.Timeslot
import com.rentmycar.entities.Timeslots
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime

class TimeSlotRepository {
    fun createTimeSlot(vehicle: Car, carAvailableFrom: LocalDateTime, carAvailableUntil: LocalDateTime): Timeslot =
        transaction {
            Timeslot.new {
                car = vehicle
                availableFrom = carAvailableFrom
                availableUntil = carAvailableUntil
            }
        }

    fun doesTimeSlotHaveConflicts(
        car: Car,
        carAvailableFrom: LocalDateTime,
        carAvailableUntil: LocalDateTime
    ): Boolean {
        val newTimeSlotRange = carAvailableFrom.rangeUntil(carAvailableUntil)

        return !getTimeSlotsByCar(car).none { existingTimeSlot ->
            (newTimeSlotRange.contains(existingTimeSlot.availableFrom) ||
                    newTimeSlotRange.contains(existingTimeSlot.availableUntil))

        }
    }

    fun getTimeSlotsById(timeSlotId: Int): Timeslot? = transaction {
        Timeslot.find { Timeslots.id eq timeSlotId }.singleOrNull()
    }

    private fun getTimeSlotsByCar(car: Car): List<Timeslot> = transaction {
        Timeslot.find { Timeslots.car eq car.id }.toList()
    }

    fun hasLinkedTimeslots(car: Car): Boolean = getTimeSlotsByCar(car).isNotEmpty()
}
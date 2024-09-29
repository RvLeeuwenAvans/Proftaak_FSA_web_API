package com.rentmycar.repositories

import com.rentmycar.entities.Car
import com.rentmycar.entities.Timeslot
import com.rentmycar.entities.Timeslots
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime

class TimeSlotRepository {

    fun createTimeSlot(vehicle: Car, carAvailableFrom: LocalDateTime, carAvailableUntil: LocalDateTime): Timeslot {
        return transaction {
            Timeslot.new {
                carId = vehicle
                availableFrom = carAvailableFrom
                availableUntil = carAvailableUntil
            }
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

    private fun getTimeSlotsByCar(car: Car): List<Timeslot> {
        return transaction {
            Timeslot.find { Timeslots.carId eq car.id }.toList()
        }
    }
}
package com.rentmycar.repositories

import com.rentmycar.entities.Car
import com.rentmycar.entities.Timeslot
import com.rentmycar.entities.Timeslots
import com.rentmycar.requests.timeslot.TimeSlotUpdateRequest
import kotlinx.datetime.toJavaLocalDateTime
import org.jetbrains.exposed.sql.and
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

    fun updateTimeSlot(timeSlot: Timeslot, data: TimeSlotUpdateRequest) = transaction {
        data.availableFrom?.let { timeSlot.availableFrom = it.toJavaLocalDateTime() }
        data.availableUntil?.let { timeSlot.availableUntil = it.toJavaLocalDateTime() }
    }

    fun deleteTimeSlot(timeSlot: Timeslot) = transaction {
        Timeslot[timeSlot.id].delete()
    }

    fun doesTimeSlotHaveConflicts(
        car: Car,
        timeSlot: Timeslot
    ): Boolean {
        val timeSlotRange = timeSlot.availableFrom.rangeUntil(timeSlot.availableFrom)

        return !getTimeSlots(car).none { existingTimeSlot ->
            (timeSlotRange.contains(existingTimeSlot.availableFrom) ||
                    timeSlotRange.contains(existingTimeSlot.availableUntil))
        }
    }

    fun doesTimeSlotHaveConflicts(
        car: Car,
        carAvailableFrom: LocalDateTime,
        carAvailableUntil: LocalDateTime
    ): Boolean {
        val newTimeSlotRange = carAvailableFrom.rangeUntil(carAvailableUntil)

        return !getTimeSlots(car).none { existingTimeSlot ->
            (newTimeSlotRange.contains(existingTimeSlot.availableFrom) ||
                    newTimeSlotRange.contains(existingTimeSlot.availableUntil))
        }
    }

    fun getTimeSlot(id: Int): Timeslot? = transaction {
        Timeslot.find { Timeslots.id eq id }.singleOrNull()
    }

    fun getTimeSlots(fromDate: LocalDateTime, tillDateTime: LocalDateTime): List<Timeslot> = transaction {
        Timeslot.find {
            (Timeslots.availableFrom.between(fromDate, tillDateTime)
                .and(Timeslots.availableUntil.between(fromDate, tillDateTime)))
        }.toList()
    }

    fun getTimeSlots(car: Car): List<Timeslot> = transaction {
        Timeslot.find { Timeslots.car eq car.id }.toList()
    }

    fun hasLinkedTimeslots(car: Car): Boolean = getTimeSlots(car).isNotEmpty()
}
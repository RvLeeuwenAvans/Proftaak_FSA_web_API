package com.rentmycar.repositories

import com.rentmycar.entities.Car
import com.rentmycar.entities.Timeslot
import com.rentmycar.entities.Timeslots
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toKotlinLocalDateTime
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction

class TimeSlotRepository {
    fun createTimeSlot(vehicle: Car, timeSlotRange: OpenEndRange<kotlinx.datetime.LocalDateTime>): Timeslot =
        transaction {
            Timeslot.new {
                car = vehicle
                availableFrom = timeSlotRange.start.toJavaLocalDateTime()
                availableUntil = timeSlotRange.endExclusive.toJavaLocalDateTime()
            }
        }

    fun getTimeSlot(id: Int): Timeslot? = transaction { Timeslot.find { Timeslots.id eq id }.singleOrNull() }

    fun getTimeSlots(timeSlotRange: OpenEndRange<kotlinx.datetime.LocalDateTime>): List<Timeslot> = transaction {
        Timeslot.find {
            Timeslots.availableFrom.between(
                timeSlotRange.start.toJavaLocalDateTime(),
                timeSlotRange.endExclusive.toJavaLocalDateTime()
            ).and(
                Timeslots.availableUntil.between(
                    timeSlotRange.start.toJavaLocalDateTime(),
                    timeSlotRange.endExclusive.toJavaLocalDateTime()
                )
            )
        }.toList()
    }

    fun getTimeSlots(car: Car, timeSlotRange: OpenEndRange<kotlinx.datetime.LocalDateTime>): List<Timeslot> =
        getTimeSlots(car).filter { existingTimeSlot ->
            (timeSlotRange.contains(existingTimeSlot.availableFrom.toKotlinLocalDateTime()) ||
                    timeSlotRange.contains(existingTimeSlot.availableUntil.toKotlinLocalDateTime()))
        }

    fun getTimeSlots(car: Car): List<Timeslot> = transaction {
        Timeslot.find { Timeslots.car eq car.id }.toList()
    }

    fun updateTimeSlot(timeSlot: Timeslot, updatedTimeSlotRange: OpenEndRange<kotlinx.datetime.LocalDateTime>) =
        transaction {
            updatedTimeSlotRange.start.let { timeSlot.availableFrom = it.toJavaLocalDateTime() }
            updatedTimeSlotRange.endExclusive.let { timeSlot.availableUntil = it.toJavaLocalDateTime() }
        }

    fun deleteTimeSlot(timeSlot: Timeslot) = transaction { Timeslot[timeSlot.id].delete() }
}
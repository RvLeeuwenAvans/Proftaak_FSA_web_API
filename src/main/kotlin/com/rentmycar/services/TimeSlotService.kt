package com.rentmycar.services

import com.rentmycar.entities.Car
import com.rentmycar.entities.Timeslot
import com.rentmycar.entities.User
import com.rentmycar.repositories.TimeSlotRepository
import com.rentmycar.services.exceptions.NotAllowedException
import com.rentmycar.services.exceptions.OverlappingTimeSlotException
import com.rentmycar.services.exceptions.TimeSlotNotFoundException
import kotlinx.datetime.*

class TimeSlotService {
    private val timeSlotRepository = TimeSlotRepository()

    fun createTimeSlot(carId: Int, user: User, timeSlotRange: OpenEndRange<LocalDateTime>) {
        val car = CarService().getCar(carId)

        if (user.id.value != car.ownerId.value) throw NotAllowedException("user is not the car's owner")
        if (timeSlotRepository.getOverlappingTimeSlots(car, timeSlotRange)
                .isNotEmpty()
        ) throw OverlappingTimeSlotException()

        timeSlotRepository.createTimeSlot(car, timeSlotRange)
    }

    fun getTimeSlot(id: Int) = timeSlotRepository.getTimeSlot(id) ?: throw TimeSlotNotFoundException(id)

    fun getTimeSlots(timeSlotRange: OpenEndRange<LocalDateTime>) =
        timeSlotRepository.getTimeSlots(timeSlotRange)

    fun getTimeSlots(car: Car) = timeSlotRepository.getTimeSlots(car)

    fun updateTimeSlot(
        timeSlotId: Int,
        user: User,
        _updatedStartDateTime: LocalDateTime? = null,
        _updatedEndDateTime: LocalDateTime? = null,
    ) {
        val timeSlot = getTimeSlot(timeSlotId)

        val updatedStartDateTime = _updatedStartDateTime ?: timeSlot.availableFrom.toKotlinLocalDateTime()
        val updatedEndDateTime = _updatedEndDateTime ?: timeSlot.availableUntil.toKotlinLocalDateTime()

        val updatedTimeSlotRange = updatedStartDateTime.rangeUntil(updatedEndDateTime)
        val car = CarService().getCar(timeSlotId)

        if (timeSlotRepository.getOverlappingTimeSlots(car, updatedTimeSlotRange)
                .any { it.id != timeSlot.id }
        ) throw OverlappingTimeSlotException()

        if (isFutureTimeSlot(timeSlot)) throw NotAllowedException("cannot edit an active or past timeslot")
        if (!isTimeSlotOwner(user, timeSlotId)) throw NotAllowedException("user is not the timeslot's owner")

        timeSlotRepository.updateTimeSlot(timeSlot, updatedTimeSlotRange)
    }

    fun deleteTimeSlot(id: Int, user: User) {
        val currentTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())

        val timeSlot = getTimeSlot(id)
        if (isFutureTimeSlot(timeSlot)) throw throw NotAllowedException("cannot delete an active or past timeslot")
        if (!isTimeSlotOwner(user, id)) throw NotAllowedException("user is not the timeslot's owner")

        timeSlotRepository.deleteTimeSlot(getTimeSlot(id))
    }

    private fun isFutureTimeSlot(timeSlot: Timeslot): Boolean {
        val currentTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        return timeSlot.availableFrom.toKotlinLocalDateTime() <= currentTime
    }

    private fun isTimeSlotOwner(user: User, timeSlotId: Int): Boolean =
        user.id.value == CarService().getCar(timeSlotId).ownerId.value
}
package com.rentmycar.services

import com.rentmycar.entities.Car
import com.rentmycar.entities.TimeslotDTO
import com.rentmycar.entities.User
import com.rentmycar.entities.toDTO
import com.rentmycar.repositories.TimeSlotRepository
import com.rentmycar.services.exceptions.NotAllowedException
import com.rentmycar.services.exceptions.NotFoundException
import com.rentmycar.services.exceptions.OverlappingTimeSlotException
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.math.sqrt

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

    fun getTimeSlot(id: Int) =
        timeSlotRepository.getTimeSlot(id) ?: throw NotFoundException("Time slot with id: $id not found")

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
        val timeSlotDTO = timeSlot.toDTO()

        val updatedStartDateTime = _updatedStartDateTime ?: timeSlotDTO.availableFrom
        val updatedEndDateTime = _updatedEndDateTime ?: timeSlotDTO.availableUntil

        val updatedTimeSlotRange = updatedStartDateTime.rangeUntil(updatedEndDateTime)
        val car = CarService().getCar(timeSlotId)

        if (timeSlotRepository.getOverlappingTimeSlots(car, updatedTimeSlotRange)
                .any { it.id.value != timeSlotDTO.id }
        ) throw OverlappingTimeSlotException()

        if (isFutureTimeSlot(timeSlotDTO)) throw NotAllowedException("cannot edit an active or past timeslot")
        CarService().ensureCarOwner(user, timeSlotDTO.carId)

        timeSlotRepository.updateTimeSlot(timeSlot, updatedTimeSlotRange)
    }

    fun deleteTimeSlot(id: Int, user: User) {
        val timeSlot = getTimeSlot(id)
        if (isFutureTimeSlot(timeSlot.toDTO())) throw throw NotAllowedException("cannot delete an active or past timeslot")
        CarService().ensureCarOwner(user, timeSlot.toDTO().carId)

        timeSlotRepository.deleteTimeSlot(timeSlot)
    }

    private fun isFutureTimeSlot(timeSlot: TimeslotDTO): Boolean {
        val currentTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        return timeSlot.availableFrom <= currentTime
    }
}


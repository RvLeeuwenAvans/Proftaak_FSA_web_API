package com.rentmycar.services

import com.rentmycar.entities.Car
import com.rentmycar.dtos.TimeslotDTO
import com.rentmycar.entities.Timeslot
import com.rentmycar.entities.User
import com.rentmycar.entities.toDTO
import com.rentmycar.repositories.TimeSlotRepository
import com.rentmycar.services.exceptions.NotAllowedException
import com.rentmycar.services.exceptions.NotFoundException
import com.rentmycar.services.exceptions.OverlappingTimeSlotException
import kotlinx.datetime.*

class TimeSlotService {
    private val timeSlotRepository = TimeSlotRepository()

    fun createTimeSlot(user: User, carId: Int, timeSlotRange: OpenEndRange<LocalDateTime>): Timeslot {
        CarService.ensureUserIsCarOwner(user, carId)

        val car = CarService.getBusinessObject(carId).getCar()

        if (timeSlotRepository.getOverlappingTimeSlots(car, timeSlotRange)
                .isNotEmpty()
        ) throw OverlappingTimeSlotException()

        return timeSlotRepository.createTimeSlot(car, timeSlotRange)
    }

    fun getTimeSlot(id: Int) =
        timeSlotRepository.getTimeSlot(id) ?: throw NotFoundException("Time slot with id: $id not found")

    fun getTimeSlots(timeSlotRange: OpenEndRange<LocalDateTime>) =
        timeSlotRepository.getTimeSlots(timeSlotRange)

    fun getTimeSlots(car: Car) = timeSlotRepository.getTimeSlots(car)

    fun updateTimeSlot(
        user: User,
        timeSlotId: Int,
        startDateTime: LocalDateTime? = null,
        endDateTime: LocalDateTime? = null,
    ) {
        val timeSlot = getTimeSlot(timeSlotId)
        val timeSlotDTO = timeSlot.toDTO()

        val updatedStartDateTime = startDateTime ?: timeSlotDTO.availableFrom
        val updatedEndDateTime = endDateTime ?: timeSlotDTO.availableUntil

        val updatedTimeSlotRange = updatedStartDateTime.rangeUntil(updatedEndDateTime)

        CarService.ensureUserIsCarOwner(user, timeSlotDTO.carId)
        val car = CarService.getBusinessObject(timeSlotDTO.carId).getCar()

        if (timeSlotRepository.getOverlappingTimeSlots(car, updatedTimeSlotRange)
                .any { it.id.value != timeSlotDTO.id }
        ) throw OverlappingTimeSlotException()

        if (isFutureTimeSlot(timeSlotDTO)) throw NotAllowedException("cannot edit an active or past timeslot")

        timeSlotRepository.updateTimeSlot(timeSlot, updatedTimeSlotRange)
        NotificationService().createTimeSlotUpdatedNotification(timeSlotId, timeSlotDTO, updatedTimeSlotRange)
    }

    fun deleteTimeSlot(user: User, timeSlotId: Int) {
        val timeSlot = getTimeSlot(timeSlotId)
        if (isFutureTimeSlot(timeSlot.toDTO())) throw throw NotAllowedException("cannot delete an active or past timeslot")

        CarService.ensureUserIsCarOwner(user, timeSlot.toDTO().carId)
        val car = CarService.getBusinessObject(timeSlot.toDTO().carId).getCar()

        NotificationService().createTimeSlotDeletedNotification(timeSlotId, car)
        timeSlotRepository.deleteTimeSlot(timeSlot)
    }

    private fun isFutureTimeSlot(timeSlot: TimeslotDTO): Boolean {
        val currentTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        return timeSlot.availableFrom <= currentTime
    }
}
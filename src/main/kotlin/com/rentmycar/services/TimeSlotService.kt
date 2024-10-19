package com.rentmycar.services

import com.rentmycar.entities.*
import com.rentmycar.repositories.TimeSlotRepository
import com.rentmycar.services.exceptions.NotAllowedException
import com.rentmycar.services.exceptions.OverlappingTimeSlotException
import com.rentmycar.services.exceptions.TimeSlotNotFoundException
import kotlinx.datetime.*
import com.rentmycar.entities.Notification
import com.rentmycar.repositories.InMemoryNotificationRepository

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
        val timeSlotDTO = timeSlot.toDTO()

        val updatedStartDateTime = _updatedStartDateTime ?: timeSlotDTO.availableFrom
        val updatedEndDateTime = _updatedEndDateTime ?: timeSlotDTO.availableUntil

        val updatedTimeSlotRange = updatedStartDateTime.rangeUntil(updatedEndDateTime)
        val car = CarService().getCar(timeSlotId)

        if (timeSlotRepository.getOverlappingTimeSlots(car, updatedTimeSlotRange)
                .any { it.id.value != timeSlotDTO.id }
        ) throw OverlappingTimeSlotException()

        if (isFutureTimeSlot(timeSlotDTO)) throw NotAllowedException("cannot edit an active or past timeslot")
        if (!CarService().isCarOwner(user, timeSlotDTO.carId)) throw NotAllowedException("user is not the timeslot's owner")

        timeSlotRepository.updateTimeSlot(timeSlot, updatedTimeSlotRange)
    }

    fun deleteTimeSlot(id: Int, user: User) {
        val timeSlot = getTimeSlot(id)
        if (isFutureTimeSlot(timeSlot.toDTO())) throw throw NotAllowedException("cannot delete an active or past timeslot")
        if (!CarService().isCarOwner(user, timeSlot.toDTO().carId)) throw NotAllowedException("user is not the timeslot's owner")

        timeSlotRepository.deleteTimeSlot(timeSlot)
    }

    private fun isFutureTimeSlot(timeSlot: TimeslotDTO): Boolean {
        val currentTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        return timeSlot.availableFrom <= currentTime
    }
}
class TimeSlotNotificationService(private val notificationRepository: InMemoryNotificationRepository) {

    fun updateTimeSlot(timeSlotId: Long, userId: Long, newStartTime: java.time.LocalDateTime, newEndTime: java.time.LocalDateTime) {
        // Update timeslot logic...

        // Create notification
        val notification = Notification(
            id = generateNotificationId(),
            userId = userId,
            message = "Your timeslot has been updated.",
            timestamp = java.time.LocalDateTime.now()
        )
        notificationRepository.createNotification(notification)
    }

    fun deleteTimeSlot(timeSlotId: Long, userId: Long) {
        // Delete timeslot logic...

        // Create notification
        val notification = Notification(
            id = generateNotificationId(),
            userId = userId,
            message = "Your timeslot has been deleted.",
            timestamp = java.time.LocalDateTime.now()
        )
        notificationRepository.createNotification(notification)
    }

    private fun generateNotificationId(): Long {
        // Implement a method to generate unique notification IDs
        return System.currentTimeMillis() // Example implementation
    }
}
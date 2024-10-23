package com.rentmycar.services


import com.rentmycar.dtos.TimeslotDTO
import com.rentmycar.entities.Car
import com.rentmycar.entities.User
import com.rentmycar.entities.toDTO
import com.rentmycar.repositories.NotificationRepository
import com.rentmycar.services.exceptions.NotFoundException
import kotlinx.datetime.LocalDateTime

class NotificationService {
    private val notificationRepository = NotificationRepository()

    fun getNotification(id: Int) =
        notificationRepository.getNotification(id) ?: throw NotFoundException("Notification: $id not found")

    fun getNotifications(user: User) = notificationRepository.getNotifications(user.id.value)

    fun deleteNotification(notificationId: Int) = notificationRepository.deleteNotification(notificationId)

    fun createTimeSlotUpdatedNotification(
        timeslotId: Int,
        oldTimeSlot: TimeslotDTO,
        updatedTimeslotRange: OpenEndRange<LocalDateTime>
    ) {
        val timeSlot = TimeSlotService().getTimeSlot(timeslotId)
        val car = CarService.getBusinessObject(timeSlot.toDTO().carId).getCar()
        val reservation = ReservationService().getReservation(timeSlot)

        createNotification(
            reservation.toDTO().reservorId,
            "Time slot updated",
            """"Your reserved time slot for car: ${car.toDTO().licensePlate} was updated.
            
            Start available from: ${oldTimeSlot.availableFrom} -> ${updatedTimeslotRange.start}
            available until: ${oldTimeSlot.availableUntil} ->  ${updatedTimeslotRange.endExclusive}""".trimIndent(),
        )
    }

    fun createTimeSlotDeletedNotification(timeslotId: Int, car: Car) {
        val timeSlot = TimeSlotService().getTimeSlot(timeslotId)
        val reservation = ReservationService().getReservation(timeSlot)

        createNotification(
            reservation.toDTO().reservorId,
            "Time slot removed",
            """"Your reserved time slot for car: ${car.toDTO().licensePlate} is canceled."""
        )
    }

    private fun createNotification(userId: Int, title: String, message: String) {
        val user = UserService().getById(userId)
        notificationRepository.createNotification(user, title, message)
    }
}
package com.rentmycar.services

import com.rentmycar.entities.Notification
import com.rentmycar.repositories.InMemoryNotificationRepository


class TimeSlotNotificationService(private val notificationRepository: InMemoryNotificationRepository) {

    fun updateTimeSlot(
        timeSlotId: Long,
        userId: Long,
        newStartTime: java.time.LocalDateTime,
        newEndTime: java.time.LocalDateTime
    ) {
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
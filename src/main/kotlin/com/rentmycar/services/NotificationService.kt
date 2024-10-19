package com.rentmycar.services


import com.rentmycar.repositories.InMemoryNotificationRepository

class NotificationService {
    private val notificationRepository = InMemoryNotificationRepository()

    fun getNotificationsByUserId(userId: Long) = notificationRepository.getNotificationsByUserId(userId)

    fun deleteNotification(notificationId: Long, userId: Long) {
        notificationRepository.deleteNotification(notificationId, userId)
    }
}
package com.rentmycar.repositories

import com.rentmycar.dtos.NotificationDTO

class InMemoryNotificationRepository {
    private val notifications = mutableListOf<NotificationDTO>()

    fun createNotification(notification: NotificationDTO) {
        notifications.add(notification)
    }

    fun getNotificationsByUserId(userId: Long): List<NotificationDTO> {
        return notifications.filter { it.userId == userId }
    }

    fun deleteNotification(notificationId: Long, userId: Long) {
        notifications.removeIf { it.id == notificationId && it.userId == userId }
    }
}
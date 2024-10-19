package com.rentmycar.repositories

import com.rentmycar.entities.Notification

class InMemoryNotificationRepository {
    private val notifications = mutableListOf<Notification>()

    fun createNotification(notification: Notification) {
        notifications.add(notification)
    }

    fun getNotificationsByUserId(userId: Long): List<Notification> {
        return notifications.filter { it.userId == userId }
    }

    fun deleteNotification(notificationId: Long, userId: Long) {
        notifications.removeIf { it.id == notificationId && it.userId == userId }
    }
}
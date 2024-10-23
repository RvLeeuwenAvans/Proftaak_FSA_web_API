package com.rentmycar.repositories

import com.rentmycar.entities.Notification
import com.rentmycar.entities.Notifications
import com.rentmycar.entities.User
import kotlinx.datetime.Clock
import kotlinx.datetime.toJavaInstant
import org.jetbrains.exposed.sql.transactions.transaction

class NotificationRepository {
    fun createNotification(
        user: User,
        subject: String,
        message: String,
    ) = transaction {
        Notification.new {
            this.user = user
            this.subject = subject
            this.message = message
            this.creationTimestamp = Clock.System.now().toJavaInstant()
        }
    }

    fun getNotification(id: Int): Notification? = transaction {
        Notification.find { Notifications.id eq id }.singleOrNull()
    }

    fun getNotifications(userId: Int): List<Notification> = transaction {
        Notification.find { Notifications.user eq userId }.toList()
    }

    fun deleteNotification(id: Int) = transaction { getNotification(id)?.delete() }
}
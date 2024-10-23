package com.rentmycar.controllers


import com.rentmycar.entities.toDTO
import com.rentmycar.plugins.user
import com.rentmycar.services.NotificationService
import com.rentmycar.utils.sanitizeId
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*

class NotificationController {
    private val notificationService = NotificationService()

    suspend fun getNotification(call: ApplicationCall) {
        val notificationId = sanitizeId(call.parameters["id"])
        val notification = notificationService.getNotification(notificationId)
        return call.respond(HttpStatusCode.OK, notification.toDTO())
    }

    suspend fun getUserNotifications(call: ApplicationCall) {
        val user = call.user()
        val notifications = notificationService.getNotifications(user)
        return call.respond(HttpStatusCode.OK, notifications.map { it.toDTO() })
    }

    suspend fun deleteNotification(call: ApplicationCall) {
        val notificationId = sanitizeId(call.parameters["id"])
        notificationService.deleteNotification(notificationId)
        return call.respond(HttpStatusCode.OK, "Notification deleted")
    }
}
package com.rentmycar.controllers


import com.rentmycar.services.NotificationService
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*

class NotificationController {
    private val notificationService = NotificationService()

    suspend fun getNotificationsByUserId(call: ApplicationCall) {
        val userId = call.principal<UserIdPrincipal>()!!.name.toLong()
        val notifications = notificationService.getNotificationsByUserId(userId)
        call.respond(notifications)
    }

    suspend fun deleteNotification(call: ApplicationCall) {
        val userId = call.principal<UserIdPrincipal>()!!.name.toLong()
        val notificationId = call.parameters["id"]!!.toLong()
        notificationService.deleteNotification(notificationId, userId)
        call.respondText("Notification deleted")
    }
}
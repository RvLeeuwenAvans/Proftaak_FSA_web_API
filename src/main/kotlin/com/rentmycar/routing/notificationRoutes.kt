package com.rentmycar.routing

import com.rentmycar.routing.controllers.NotificationController
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.routing.*

import io.ktor.server.application.*
import io.ktor.server.response.* // Import for call.respond


fun Route.notificationRoutes() {
    val notificationController = NotificationController()

    authenticate {
        route("/notifications") {
            get { notificationController.getNotificationsByUserId(call) }
            delete("/{id}") { notificationController.deleteNotification(call) }
        }
    }
}
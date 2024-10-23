package com.rentmycar.routing

import com.rentmycar.controllers.NotificationController
import io.ktor.server.auth.*
import io.ktor.server.routing.*


fun Route.notificationRoutes() {
    val notificationController = NotificationController()

    authenticate {
        route("/notifications") {
            get { notificationController.getNotificationsByUserId(call) }
            delete("/{id}") { notificationController.deleteNotification(call) }
        }
    }
}
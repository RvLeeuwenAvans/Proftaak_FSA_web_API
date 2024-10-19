package com.rentmycar.routing

import com.rentmycar.controllers.ImageController
import io.ktor.server.auth.*
import io.ktor.server.http.content.*
import io.ktor.server.routing.*
import java.io.File

fun Route.imageRoutes() {
    val imageController = ImageController()

    authenticate {
        route("/image/{carId}") {
            get { imageController.getImages(call) }
            post { imageController.uploadImage(call) }
        }

        staticFiles(remotePath = "/images", dir = File("uploads"))
    }
}
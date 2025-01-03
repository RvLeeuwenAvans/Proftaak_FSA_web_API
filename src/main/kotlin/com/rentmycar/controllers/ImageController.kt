package com.rentmycar.controllers

import com.rentmycar.entities.Car
import com.rentmycar.plugins.user
import com.rentmycar.services.CarService
import com.rentmycar.services.ImageService
import com.rentmycar.utils.sanitizeId
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.utils.io.*
import kotlinx.io.readByteArray
import java.io.File
import java.util.*

class ImageController {
    private val imageService = ImageService()

    suspend fun getImages(call: ApplicationCall) {
        val carId = sanitizeId(call.parameters["id"])
        val images = imageService.getByCar(carId)

        call.respond(HttpStatusCode.OK, images.map { it.path })
    }

    suspend fun uploadImage(call: ApplicationCall) {
        val carId = sanitizeId(call.parameters["id"])

        imageService.delete(carId)

        CarService.ensureUserIsCarOwner(call.user(), carId)
        val car = CarService.getBusinessObject(carId).getCar()

        UploadService.uploadImages(call.receiveMultipart(), car)

        call.respond(HttpStatusCode.OK)
    }

    private object UploadService {
        suspend fun uploadImages(multiPartData: MultiPartData, car: Car) {
            multiPartData.forEachPart { part ->
                when {
                    part is PartData.FileItem -> {
                        uploadImage(part, car)
                    }
                }
                part.dispose()
            }
        }

        suspend fun uploadImage(part: PartData.FileItem, car: Car) {
            val fileName = UUID.randomUUID().toString()
            val fileExtension = part.originalFileName?.substringAfterLast('.', "")
            val path = "$fileName.$fileExtension"
            val fileBytes = part.provider().readRemaining().readByteArray()
            File("uploads/$path").writeBytes(fileBytes)
            ImageService().create(path, car)
        }
    }
}


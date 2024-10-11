package com.rentmycar.controllers

import com.rentmycar.entities.Image
import com.rentmycar.plugins.user
import com.rentmycar.repositories.CarRepository
import com.rentmycar.repositories.ImageRepository
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.response.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.utils.io.*
import kotlinx.io.readByteArray
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.File
import java.util.*

class ImageController {
    private val imageRepository = ImageRepository()
    private val carRepository = CarRepository()

    suspend fun getImages(call: ApplicationCall) {
        val carId = call.parameters["carId"]?.toInt() ?: throw IllegalArgumentException("Car ID is missing or invalid")
        val images = imageRepository.getByCar(carId)

        call.respond(images.map { it.path })
    }
    suspend fun uploadImage(call: ApplicationCall) {
        val user = call.user();
        val carId = call.parameters["carId"]?.toInt() ?: throw IllegalArgumentException("Car ID is missing or invalid")
        val images = imageRepository.getByCar(carId)
        deleteImages(images)
        val car = carRepository.getUserCarById(carId, user.id)
        val multipartData = call.receiveMultipart()

        multipartData.forEachPart { part ->
            when (part) {
                is PartData.FileItem -> {
                    val fileName = UUID.randomUUID().toString()
                    val fileExtension = part.originalFileName?.substringAfterLast('.', "")
                    val path = "$fileName.$fileExtension"
                    val fileBytes = part.provider().readRemaining().readByteArray()
                    File("uploads/$path").writeBytes(fileBytes)
                    imageRepository.createImage(path, car)
                }

                else -> {}
            }
            part.dispose()
        }

        call.respond(HttpStatusCode.OK)
    }

    private fun deleteImages(images: List<Image>){
        for(image in images){
            File("uploads/${image.path}").delete()
            transaction {
                image.delete();
            }
        }
    }
}
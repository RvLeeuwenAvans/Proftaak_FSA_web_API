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
        val imagePaths = imageService.getCarImagePaths(carId)

        call.respond(HttpStatusCode.OK, imagePaths)
    }

suspend fun uploadImage(call: ApplicationCall) {
    try {
        val carId = sanitizeId(call.parameters["id"])
        println("Uploading image for car ID: $carId")

        imageService.delete(carId)
        println("Deleted existing images for car ID: $carId")

        CarService.ensureUserIsCarOwner(call.user(), carId)
        println("User ownership verified for car ID: $carId")

        val car = CarService.getBusinessObject(carId).getCar()
        println("Retrieved car object for ID: $carId")

        val multipart = call.receiveMultipart()
        println("Received multipart data")

        multipart.forEachPart { part ->
            when (part) {
                is PartData.FileItem -> {
                    println("Processing file item: ${part.originalFileName}")
                    val fileName = UUID.randomUUID().toString()
                    val fileExtension = part.originalFileName?.substringAfterLast('.', "")
                    val path = "$fileName.$fileExtension"
                    println("Generated file path: $path")

                    val fileBytes = part.provider().readRemaining().readByteArray()
                    println("Read ${fileBytes.size} bytes from file")

                    val uploadsDir = File("uploads")
                    if (!uploadsDir.exists()) {
                        val created = uploadsDir.mkdirs()
                        println("Created uploads directory: $created")
                    }

                    val file = File(uploadsDir, path)
                    println("Writing to file: ${file.absolutePath}")

                    file.writeBytes(fileBytes)
                    println("File written successfully")

                    ImageService().create(path, car)
                    println("Image record created in database")
                }
                else -> {
                    println("Skipping non-file part: ${part.name}")
                }
            }
            part.dispose()
        }

        call.respond(HttpStatusCode.OK)
        println("Upload completed successfully")
    } catch (e: Exception) {
        println("Error in uploadImage: ${e.message}")
        e.printStackTrace()
        call.respond(HttpStatusCode.InternalServerError, "Failed to upload image: ${e.message}")
    }
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

    println("Current working directory: ${System.getProperty("user.dir")}")
    val uploadsDir = File("uploads")
    println("Uploads directory exists: ${uploadsDir.exists()}")
    println("Uploads directory is writable: ${uploadsDir.canWrite()}")

    val file = File(uploadsDir, path)
    println("Attempting to write to: ${file.absolutePath}")

    try {
        if (!uploadsDir.exists()) {
            val created = uploadsDir.mkdirs()
            println("Created uploads directory: $created")
        }
        file.writeBytes(fileBytes)
        println("File written successfully")
        ImageService().create(path, car)
    } catch (e: Exception) {
        println("Error writing file: ${e.message}")
        e.printStackTrace()
        throw Exception("Failed to save image: ${e.message}")
    }
}
    }
}


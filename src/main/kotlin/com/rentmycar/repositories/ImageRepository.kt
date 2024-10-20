package com.rentmycar.repositories

import com.rentmycar.entities.Car
import com.rentmycar.entities.Image
import com.rentmycar.entities.Images
import org.jetbrains.exposed.sql.transactions.transaction

class ImageRepository {
    fun createImage(path: String, car: Car): Image {
        return transaction {
            Image.new {
                this.path = path
                this.car = car
            }
        }
    }

    fun getByCar(carId: Int): List<Image> = transaction {
        Image.find { Images.car eq carId }.toList()
    }

    fun delete(image: Image) = transaction { image.delete() }
}
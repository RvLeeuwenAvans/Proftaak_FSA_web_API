package com.rentmycar.services

import com.rentmycar.entities.Car
import com.rentmycar.entities.Image
import com.rentmycar.repositories.ImageRepository

class ImageService {
    private val imageRepository = ImageRepository()

    fun getByCar(carId: Int): List<Image> = imageRepository.getByCar(carId)

    fun create(path: String, car: Car): Image = imageRepository.createImage(path, car)

    fun delete(carId: Int) {
        val images = getByCar(carId)
        for (image in images) {
            imageRepository.delete(image)
        }
    }
}
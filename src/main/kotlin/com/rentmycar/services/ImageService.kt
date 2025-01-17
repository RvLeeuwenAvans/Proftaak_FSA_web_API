package com.rentmycar.services

import com.rentmycar.entities.Car
import com.rentmycar.entities.Image
import com.rentmycar.repositories.ImageRepository

class ImageService {
    private val imageRepository = ImageRepository()

    fun getCarImagePaths(carId: Int): List<String> = imageRepository.getCarImagePaths(carId)

    fun create(path: String, car: Car): Image = imageRepository.createImage(path, car)

    fun delete(carId: Int) {
        val images = imageRepository.getByCar(carId)
        for (image in images) {
            imageRepository.delete(image)
        }
    }
}
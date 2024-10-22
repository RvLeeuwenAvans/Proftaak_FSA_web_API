package com.rentmycar.services

import com.rentmycar.entities.User
import com.rentmycar.repositories.UserRepository
import com.rentmycar.requests.user.UserRegistrationRequest
import com.rentmycar.requests.user.UserUpdateRequest
import com.rentmycar.services.exceptions.AlreadyExistsException
import com.rentmycar.services.exceptions.NotFoundException
import com.rentmycar.utils.UserRole

class UserService {
    private val userRepository = UserRepository()

    fun checkAlreadyExist(username: String, email: String) {
        if (userRepository.doesUserExistByUsername(username)) {
            throw AlreadyExistsException("User with username $username already exists")
        }

        if (userRepository.doesUserExistByEmail(email)) {
            throw AlreadyExistsException("User with email $email already exists")
        }
    }

    fun create(registrationRequest: UserRegistrationRequest, role: UserRole): User =
        userRepository.createUser(registrationRequest, role)

    fun getByEmail(email: String): User =
        userRepository.getUserByEmail(email) ?: throw NotFoundException("User with email $email not found")

    fun update(user: User, updateRequest: UserUpdateRequest): User = userRepository.updateUser(user, updateRequest)
    fun delete(user: User) = userRepository.deleteUser(user)
}
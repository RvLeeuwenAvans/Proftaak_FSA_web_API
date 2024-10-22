package com.rentmycar.services

import com.rentmycar.entities.User
import com.rentmycar.repositories.UserRepository
import com.rentmycar.controllers.requests.user.UserRegistrationRequest
import com.rentmycar.controllers.requests.user.UserUpdateRequest
import com.rentmycar.services.exceptions.AlreadyExistsException
import com.rentmycar.services.exceptions.NotFoundException
import com.rentmycar.utils.UserRole

class UserService {
    private val userRepository = UserRepository()

    fun create(registrationRequest: UserRegistrationRequest, role: UserRole): User {
        checkAlreadyExist(registrationRequest.username, registrationRequest.email)
        return userRepository.createUser(registrationRequest, role)
    }

    fun getByEmail(email: String): User =
        userRepository.getUserByEmail(email) ?: throw NotFoundException("User with email $email not found")

    fun update(user: User, updateRequest: UserUpdateRequest): User = userRepository.updateUser(user, updateRequest)
    fun delete(user: User) = userRepository.deleteUser(user)

    private fun checkAlreadyExist(username: String, email: String) {
        when {
            userRepository.doesUserExistByUsername(username) ->"User with username $username already exists"
            userRepository.doesUserExistByEmail(email) -> "User with email $email already exists"
            else -> return
        }.let { throw AlreadyExistsException(it) }
    }
}
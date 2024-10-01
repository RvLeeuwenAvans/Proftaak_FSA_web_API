package com.rentmycar.repositories

import com.rentmycar.authentication.PasswordHasher
import com.rentmycar.entities.User
import com.rentmycar.entities.Users
import com.rentmycar.requests.user.UserRegistrationRequest
import com.rentmycar.requests.user.UserUpdateRequest
import io.ktor.server.plugins.*
import org.jetbrains.exposed.sql.transactions.transaction

class UserRepository {
    fun getUserByEmail(email: String): User? = transaction {
        User.find { Users.email eq email }.singleOrNull()
    }

    internal fun getUserById(id: Int): User = transaction {
        User.find { Users.id eq id }.singleOrNull() ?: throw NotFoundException("User with id $id not found");
    }

    private fun getUserByUsername(username: String): User? = transaction {
        User.find { Users.username eq username }.singleOrNull()
    }


    fun createUser(request: UserRegistrationRequest): User = transaction {
        User.new {
            firstName = request.firstName
            lastName = request.lastName
            username = request.username
            email = request.email
            password = PasswordHasher.hashPassword(request.password)
        }
    }

    fun updateUser(user: User, data: UserUpdateRequest) = transaction {
        data.firstName?.let { user.firstName = it }
        data.lastName?.let { user.lastName = it }
        data.username?.let { user.username = it }
        data.email?.let { user.email = it }
        data.password?.let { user.password = PasswordHasher.hashPassword(it) }
    }


    fun doesUserExistByEmail(email: String) = getUserByEmail(email) != null

    fun doesUserExistByUsername(username: String): Boolean = getUserByUsername(username) != null
}
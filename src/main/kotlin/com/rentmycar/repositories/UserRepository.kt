package com.rentmycar.repositories

import com.rentmycar.authentication.PasswordHasher
import com.rentmycar.entities.User
import com.rentmycar.entities.Users
import com.rentmycar.dtos.requests.user.UserRegistrationRequest
import com.rentmycar.dtos.requests.user.UserUpdateRequest
import com.rentmycar.utils.UserRole
import io.ktor.server.plugins.*
import org.jetbrains.exposed.sql.transactions.transaction

class UserRepository {
    fun getUserByEmail(email: String): User? = transaction {
        User.find { Users.email eq email }.singleOrNull()
    }

    fun getUserById(id: Int): User = transaction {
        User.find { Users.id eq id }.singleOrNull() ?: throw NotFoundException("User with id $id not found")
    }

    private fun getUserByUsername(username: String): User? = transaction {
        User.find { Users.username eq username }.singleOrNull()
    }


    fun createUser(request: UserRegistrationRequest, role: UserRole): User = transaction {
        User.new {
            firstName = request.firstName
            lastName = request.lastName
            username = request.username
            email = request.email
            password = PasswordHasher.hashPassword(request.password)
            this.role = role
        }
    }

    fun updateUser(user: User, data: UserUpdateRequest): User = transaction {
        user.apply {
            data.firstName?.let { user.firstName = it }
            data.lastName?.let { user.lastName = it }
            data.username?.let { user.username = it }
            data.email?.let { user.email = it }
            data.password?.let { user.password = PasswordHasher.hashPassword(it) }
            data.role?.let { user.role = UserRole.valueOf(it) }
        }
    }

    fun deleteUser(user: User) = transaction { user.delete() }

    fun doesUserExistByEmail(email: String) = getUserByEmail(email) != null

    fun doesUserExistByUsername(username: String): Boolean = getUserByUsername(username) != null

    fun getUserRole(id: Int): UserRole = getUserById(id).role
}
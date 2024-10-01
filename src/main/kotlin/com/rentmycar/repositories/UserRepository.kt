package com.rentmycar.repositories

import com.rentmycar.authentication.PasswordHasher
import com.rentmycar.entities.User
import com.rentmycar.entities.Users
import com.rentmycar.requests.user.RegistrationRequest
import org.jetbrains.exposed.sql.transactions.transaction

class UserRepository {
    fun getUserByEmail(email: String): User? = transaction {
        User.find { Users.email eq email }.singleOrNull()
    }

    internal fun getUserById(id: Int): User = transaction {
        User.find { Users.id eq id }.single()
    }

    private fun getUserByUsername(username: String): User? = transaction {
        User.find { Users.username eq username }.singleOrNull()
    }


    fun createUser(request: RegistrationRequest): User = transaction {
        User.new {
            firstName = request.firstName
            lastName = request.lastName
            username = request.username
            email = request.email
            password = PasswordHasher.hashPassword(request.password)
        }
    }

    fun doesUserExistByEmail(email: String) = getUserByEmail(email) != null

    fun doesUserExistByUsername(username: String): Boolean = getUserByUsername(username) != null
}
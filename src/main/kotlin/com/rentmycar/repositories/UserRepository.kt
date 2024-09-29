package com.rentmycar.repositories

import com.rentmycar.authentication.PasswordHasher
import com.rentmycar.entities.User
import com.rentmycar.entities.Users
import com.rentmycar.requests.RegistrationRequest
import org.jetbrains.exposed.sql.transactions.transaction

class UserRepository {

    fun getUserByEmail(email: String): User? {
        return transaction {
            User.find { Users.email eq email }.singleOrNull()
        }
    }

    internal fun getUserById(id: Int): User? {
        return transaction {
            User.find { Users.id eq id }.singleOrNull()
        }
    }

    private fun getUserByUsername(username: String): User? {
        return transaction {
            User.find { Users.username eq username }.singleOrNull()
        }
    }

    fun createUser(request: RegistrationRequest): User {
        return transaction {
            User.new {
                firstName = request.firstName
                lastName = request.lastName
                username = request.username
                email = request.email
                password = PasswordHasher.hashPassword(request.password)
            }
        }
    }

    fun doesUserExistByEmail(email: String): Boolean {
        return getUserByEmail(email) != null
    }

    fun doesUserExistByUsername(username: String): Boolean {
        return getUserByUsername(username) != null
    }

}
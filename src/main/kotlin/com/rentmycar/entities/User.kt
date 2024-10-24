package com.rentmycar.entities

import com.rentmycar.utils.UserRole
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

object Users : IntIdTable() {
    val firstName = varchar("first_name", 50)
    val lastName = varchar("last_name", 50)
    val username = varchar("username", 50).uniqueIndex()
    val email = varchar("email", 255).uniqueIndex()
    val password = varchar("password", 64)
    val role = enumerationByName("role", 50, UserRole::class)
    val score = integer("score").default(0)
}

class User(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<User>(Users)

    var firstName by Users.firstName
    var lastName by Users.lastName
    var username by Users.username
    var email by Users.email
    var password by Users.password
    var role by Users.role
    var score by Users.score
}
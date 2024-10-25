package com.rentmycar.user

import com.rentmycar.BaseTest
import com.rentmycar.entities.User
import com.rentmycar.services.AccelerationService
import com.rentmycar.services.UserService
import com.rentmycar.utils.UserRole
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class RewardUserTest: BaseTest() {
    private lateinit var user: User

    @BeforeTest
    fun initUser() {
        user = transaction {
            User.new {
                firstName = "Test"
                lastName = "User"
                username = "testuser4444"
                email = "testuser4444@gmail.com"
                password = "fakepwd"
                role = UserRole.DEFAULT
            }
        }
    }

    @Test
    fun successfulUpdatedUserScore() {
        val score = AccelerationService().getScore(0.1f, -1.5f, 1.1f)
        var updatedUser = UserService().updateUserScore(user, score)
        assertEquals(100, score)
        assertEquals(50, updatedUser.score)

        val score2 = AccelerationService().getScore(3f, -8.5f, 4.1f)
        updatedUser = UserService().updateUserScore(user, score2)
        assertEquals(80, score2)
        assertEquals(65, updatedUser.score)
    }
}
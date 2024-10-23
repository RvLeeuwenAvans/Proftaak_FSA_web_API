package com.rentmycar.entities

import com.rentmycar.dtos.NotificationDTO
import kotlinx.datetime.toKotlinInstant
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.javatime.timestamp
import org.jetbrains.exposed.sql.transactions.transaction

object Notifications : IntIdTable() {
    val user = reference("user_id", Users, ReferenceOption.CASCADE)
    val subject = varchar("subject", 255)
    val message = varchar("message", 255)
    val creationTimestamp = timestamp("created_at")
}

class Notification(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Notification>(Notifications)
    var user by User referencedOn Notifications.user
    var subject by Notifications.subject
    var message by Notifications.message
    var creationTimestamp by Notifications.creationTimestamp
}

fun Notification.toDTO(): NotificationDTO = transaction {
    NotificationDTO(
        id = this@toDTO.id.value,
        userId = this@toDTO.user.id.value,
        subject = this@toDTO.subject,
        message = this@toDTO.message,
        timestamp = this@toDTO.creationTimestamp.toKotlinInstant()
    )
}

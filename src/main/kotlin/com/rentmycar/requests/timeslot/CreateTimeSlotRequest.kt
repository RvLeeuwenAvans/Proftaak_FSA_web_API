package com.rentmycar.requests.timeslot

import kotlinx.datetime.Clock
import kotlinx.serialization.Serializable
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.Contextual

@Serializable
data class CreateTimeSlotRequest(
    val carId: Int,
    @Contextual val availableFrom: LocalDateTime,
    @Contextual val availableUntil: LocalDateTime,
) {
    fun validate(): List<String> {
        val errors = mutableListOf<String>()

        if (carId <= 0) errors.add("No car Id given")

        if (availableFrom < Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()))
            errors.add("The start of a timeslot cannot be in the past")

        if (availableUntil < Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()))
            errors.add("The end of a timeslot cannot be in the past")

        return errors
    }
}

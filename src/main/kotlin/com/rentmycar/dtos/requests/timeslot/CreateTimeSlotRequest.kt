package com.rentmycar.dtos.requests.timeslot

import com.rentmycar.services.exceptions.RequestValidationException
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

@Serializable
data class CreateTimeSlotRequest(
    val carId: Int,
    @Contextual val availableFrom: LocalDateTime,
    @Contextual val availableUntil: LocalDateTime,
) {
    fun validate() {
        val errors = mutableListOf<String>()

        if (carId <= 0) errors.add("No car Id given")

        if (availableFrom < Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()))
            errors.add("The start of a timeslot cannot be in the past")

        if (availableUntil < Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()))
            errors.add("The end of a timeslot cannot be in the past")

        if (availableFrom > availableUntil) errors.add("The end of a timeslot cannot be before the start")

        if (errors.isNotEmpty()) {
            throw RequestValidationException(errors)
        }
    }
}

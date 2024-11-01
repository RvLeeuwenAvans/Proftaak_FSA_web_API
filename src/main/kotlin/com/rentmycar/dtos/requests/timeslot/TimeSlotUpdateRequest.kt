package com.rentmycar.dtos.requests.timeslot

import com.rentmycar.services.exceptions.RequestValidationException
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

@Serializable
class TimeSlotUpdateRequest(
    val timeSlotId: Int,
    @Contextual val availableFrom: LocalDateTime? = null,
    @Contextual val availableUntil: LocalDateTime? = null
) {
    fun validate() {
        val errors = mutableListOf<String>()

        if (timeSlotId <= 0) errors.add("No time slot Id given")

        if (availableFrom != null && availableFrom < Clock.System.now()
                .toLocalDateTime(TimeZone.currentSystemDefault())
        ) errors.add("The start of a time slot cannot be in the past")

        if (availableUntil != null && availableUntil < Clock.System.now()
                .toLocalDateTime(TimeZone.currentSystemDefault())
        ) errors.add("The end of a time slot cannot be in the past")

        if (
            availableFrom != null &&
            availableUntil != null &&
            availableFrom > availableUntil
        ) errors.add("The end of a time slot cannot be before the start")

        if (errors.isNotEmpty()) {
            throw RequestValidationException(errors)
        }
    }
}

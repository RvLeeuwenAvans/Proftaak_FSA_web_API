package com.rentmycar.utils

import com.rentmycar.dtos.requests.car.RegisterCarRequest
import com.rentmycar.dtos.requests.location.LocationRequest
import com.rentmycar.dtos.requests.reservation.CreateReservationRequest
import com.rentmycar.dtos.requests.timeslot.CreateTimeSlotRequest
import com.rentmycar.dtos.requests.user.UserRegistrationRequest
import kotlinx.datetime.toKotlinLocalDateTime
import java.time.LocalDateTime
import kotlin.random.Random

val brands = listOf(
    "Tesla",
    "Toyota",
    "BMW",
    "Ford",
)

val models = listOf(
    Pair("Model 1", "Tesla"),
    Pair("Model 2", "Tesla"),

    Pair("Model 3", "Toyota"),
    Pair("Model 4", "Toyota"),

    Pair("Model 5", "BMW"),
    Pair("Model 6", "BMW"),

    Pair("Model 7", "Ford"),
    Pair("Model 8", "Ford"),
)

val users = listOf(
    UserRegistrationRequest("Test", "User", "test.user", "test.user@test.com", "fakepwd"),
    UserRegistrationRequest("John", "Doe", "john.doe", "john.doe@test.com", "fakepwd"),
    UserRegistrationRequest("Test", "Admin", "test.admin", "test.admin@student.avans.nl", "fakepwd"),
    UserRegistrationRequest("Second", "Admin", "second.admin", "second.admin@student.avans.nl", "fakepwd"),
)

val cars = listOf(
    RegisterCarRequest("CB1444BB", 1, "DIESEL",   2004, "Pink",            "AUTOMATIC", 4444.0),
    RegisterCarRequest("CB2444BB", 1, "PETROL",   2004, "Light Pink",      "AUTOMATIC", 444.0),
    RegisterCarRequest("CB3444BB", 2, "GAS",      2004, "Dark Pink",       "AUTOMATIC", 100.0),
    RegisterCarRequest("CB4444BB", 2, "ELECTRIC", 2004, "Violet",          "AUTOMATIC", 666.6),
    RegisterCarRequest("CB5444BB", 3, "ELECTRIC", 2004, "Pink",            "AUTOMATIC", 6666.0),
    RegisterCarRequest("CB6444BB", 4, "HYDROGEN", 2004, "Pink and Yellow", "AUTOMATIC", 444.4),
)

val locations = listOf(
    LocationRequest(1, -0.116773, 51.510357),
    LocationRequest(4, 11.114444, 29.560351),
)

val now = LocalDateTime.now()
val timeslots = listOf(
    CreateTimeSlotRequest(
        1,
        now.toKotlinLocalDateTime(),
        now.plusMinutes(1).toKotlinLocalDateTime()
    ),
    CreateTimeSlotRequest(
        1,
        now.plusDays(1).toKotlinLocalDateTime(),
        now.plusDays(1).plusMinutes(1).toKotlinLocalDateTime()
    ),
    CreateTimeSlotRequest(
        4,
        now.toKotlinLocalDateTime(),
        now.plusMinutes(1).toKotlinLocalDateTime()
    ),
)

val reservations = listOf(
    CreateReservationRequest(1),
    CreateReservationRequest(3),
)

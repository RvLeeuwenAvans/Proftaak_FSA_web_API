package com.rentmycar.repositories.exceptions

class UserNotFoundException(override val message: String?) : RuntimeException(message)
package com.rentmycar.services.exceptions

class RequestValidationException(val errors: List<String>) :
    Exception("Validation failed with ${errors.size} error(s).")